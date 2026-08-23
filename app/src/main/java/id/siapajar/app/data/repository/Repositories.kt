package id.siapajar.app.data.repository

import android.content.Context
import androidx.work.*
import id.siapajar.app.data.local.TokenManager
import id.siapajar.app.data.local.dao.*
import id.siapajar.app.data.local.entity.*
import id.siapajar.app.data.remote.*
import id.siapajar.app.domain.model.*
import id.siapajar.app.worker.SyncWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.UUID

class AuthRepository(
    private val context: Context,
    private val tokenManager: TokenManager = TokenManager.getInstance(context)
) {
    suspend fun login(email: String, pass: String): Result<UserProfileDto> = withContext(Dispatchers.IO) {
        try {
            val apiService = ApiClient.getApiService(context)
            val response = apiService.login(LoginRequest(email.trim(), pass))
            if (response.isSuccessful && response.body()?.data != null) {
                val loginData = response.body()!!.data!!
                tokenManager.saveSession(
                    token = loginData.token,
                    fullName = loginData.user.fullName,
                    email = loginData.user.email,
                    schoolName = loginData.user.schoolName,
                    educationLevel = loginData.user.educationLevel,
                    role = loginData.user.role
                )
                Result.success(loginData.user)
            } else {
                val errorBodyStr = response.errorBody()?.string()
                val parsedErrorMsg = try {
                    if (!errorBodyStr.isNullOrBlank()) {
                        val regex = """"message"\s*:\s*"([^"]+)"""".toRegex()
                        regex.find(errorBodyStr)?.groupValues?.get(1)
                    } else null
                } catch (_: Exception) { null }

                val errorMsg = parsedErrorMsg
                    ?: response.body()?.message
                    ?: if (response.code() == 401) "Email atau kata sandi salah" else "Gagal masuk (${response.code()})"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            val friendlyMsg = when {
                e.message?.contains("Failed to connect") == true || e.message?.contains("ECONNREFUSED") == true ->
                    "Tidak dapat terhubung ke server SiapAjar. Pastikan backend aktif."
                else -> e.localizedMessage ?: "Gagal terhubung ke server backend"
            }
            Result.failure(Exception(friendlyMsg))
        }
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        tokenManager.clearSession()
    }

    fun isLoggedIn(): Boolean = tokenManager.isLoggedIn()

    fun getSession() = tokenManager.sessionState

    fun updateBaseUrl(url: String) = tokenManager.saveBaseUrl(url)
}

class AssessmentRepository(
    private val assessmentDao: AssessmentDao,
    private val context: Context
) {
    private val apiService: SiapAjarApiService
        get() = ApiClient.getApiService(context)

    fun getAllAssessments(): Flow<List<Assessment>> =
        assessmentDao.getAllAssessments().map { entities ->
            entities.map { it.toDomain() }
        }

    fun getAssessmentsForStudent(studentId: String): Flow<List<Assessment>> =
        assessmentDao.getAssessmentsForStudent(studentId).map { entities ->
            entities.map { it.toDomain() }
        }

    suspend fun saveAssessment(
        studentIds: List<String>,
        studentNames: List<String>,
        instrumentType: InstrumentType,
        photoPath: String?,
        notes: String,
        tpCode: String? = null,
        tpTitle: String? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val id = UUID.randomUUID().toString()
        val entity = AssessmentEntity(
            id = id,
            studentIdsJson = studentIds.joinToString(","),
            studentNamesJson = studentNames.joinToString(", "),
            instrumentType = instrumentType.name,
            photoPath = photoPath,
            notes = notes,
            tpCode = tpCode,
            tpTitle = tpTitle,
            syncStatus = SyncStatus.PENDING.name,
            createdAt = System.currentTimeMillis()
        )
        // 1. Optimistic write to local Room SQLite
        assessmentDao.insertAssessment(entity)

        // 2. Try online upload directly first
        try {
            val classIdPart = "1".toRequestBody("text/plain".toMediaTypeOrNull())
            val studentIdsPart = studentIds.joinToString(",").toRequestBody("text/plain".toMediaTypeOrNull())
            val instrumentPart = instrumentType.name.toRequestBody("text/plain".toMediaTypeOrNull())
            val notesPart = notes.toRequestBody("text/plain".toMediaTypeOrNull())

            var photoPart: MultipartBody.Part? = null
            if (!photoPath.isNullOrBlank()) {
                val file = File(photoPath)
                if (file.exists()) {
                    val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    photoPart = MultipartBody.Part.createFormData("photo", file.name, requestFile)
                }
            }

            val response = apiService.uploadAssessment(
                photo = photoPart,
                classId = classIdPart,
                studentIds = studentIdsPart,
                instrumentType = instrumentPart,
                notes = notesPart
            )

            if (response.isSuccessful) {
                assessmentDao.updateSyncStatus(id, SyncStatus.SYNCED.name)
                return@withContext Result.success(Unit)
            }
        } catch (_: Exception) {
            // Offline or network error: schedule background sync via WorkManager
        }

        triggerSyncWorker()
        Result.success(Unit)
    }

    private fun triggerSyncWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "assessment_sync",
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }

    private fun AssessmentEntity.toDomain() = Assessment(
        id = id,
        studentIds = studentIdsJson.split(",").filter { it.isNotBlank() },
        studentNames = studentNamesJson.split(", ").filter { it.isNotBlank() },
        instrumentType = try {
            InstrumentType.valueOf(instrumentType)
        } catch (e: Exception) {
            InstrumentType.CATATAN_ANEKDOT
        },
        photoPath = photoPath,
        notes = notes,
        tpCode = tpCode,
        tpTitle = tpTitle,
        syncStatus = SyncStatus.valueOf(syncStatus),
        createdAt = createdAt
    )
}

class AttendanceRepository(
    private val attendanceDao: AttendanceDao,
    private val context: Context
) {
    private val apiService: SiapAjarApiService
        get() = ApiClient.getApiService(context)

    fun getAttendancesByDate(date: String): Flow<List<Attendance>> =
        attendanceDao.getAttendancesByDate(date).map { entities ->
            entities.map { it.toDomain() }
        }

    suspend fun saveAttendances(items: List<Attendance>, date: String, classId: String): Result<Unit> = withContext(Dispatchers.IO) {
        val entities = items.map {
            AttendanceEntity(
                id = it.id,
                studentId = it.studentId,
                studentName = it.studentName,
                date = it.date,
                status = it.status.code,
                notes = it.notes,
                syncStatus = SyncStatus.PENDING.name
            )
        }
        attendanceDao.insertAttendances(entities)

        // Try direct upload to API
        try {
            val req = AttendanceSubmitRequest(
                date = date,
                classId = classId,
                items = items.map { AttendanceItemDto(it.studentId, it.status.code, it.notes) }
            )
            val response = apiService.submitAttendance(req)
            if (response.isSuccessful) {
                return@withContext Result.success(Unit)
            }
        } catch (_: Exception) {
            // Keep in local Room database
        }

        Result.success(Unit)
    }

    private fun AttendanceEntity.toDomain() = Attendance(
        id = id,
        studentId = studentId,
        studentName = studentName,
        date = date,
        status = AttendanceStatus.values().firstOrNull { it.code == status } ?: AttendanceStatus.HADIR,
        notes = notes,
        syncStatus = SyncStatus.valueOf(syncStatus)
    )
}

class StudentRepository(
    private val studentDao: StudentDao,
    private val context: Context
) {
    private val apiService: SiapAjarApiService
        get() = ApiClient.getApiService(context)

    fun getStudentsByClass(classId: String): Flow<List<Student>> =
        studentDao.getStudentsByClass(classId).map { entities ->
            entities.map {
                Student(
                    id = it.id,
                    name = it.name,
                    nis = it.nis,
                    photoUrl = it.photoUrl,
                    classId = it.classId,
                    className = it.className
                )
            }
        }

    suspend fun fetchClasses(): List<ClassDto> = withContext(Dispatchers.IO) {
        try {
            val res = apiService.getClasses()
            if (res.isSuccessful && res.body()?.data != null) {
                return@withContext res.body()!!.data!!
            }
        } catch (_: Exception) {}
        emptyList()
    }

    suspend fun fetchTodayAgenda(classId: String): TodayAgendaDto? = withContext(Dispatchers.IO) {
        try {
            val res = apiService.getTodayAgenda(classId)
            if (res.isSuccessful && res.body()?.data != null) {
                return@withContext res.body()!!.data!!
            }
        } catch (_: Exception) {}
        null
    }

    suspend fun fetchStudentTimeline(studentId: String): List<StudentTimelineDto> = withContext(Dispatchers.IO) {
        try {
            val res = apiService.getStudentTimeline(studentId)
            if (res.isSuccessful && res.body()?.data != null) {
                return@withContext res.body()!!.data!!
            }
        } catch (_: Exception) {}
        emptyList()
    }

    suspend fun fetchStudentsFromApi(classId: String) = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getStudents(classId)
            if (response.isSuccessful && response.body()?.data != null) {
                val dtos = response.body()!!.data!!
                val entities = dtos.map {
                    StudentEntity(
                        id = it.id,
                        name = it.name,
                        nis = it.nis,
                        photoUrl = it.avatarUrl,
                        classId = it.classId,
                        className = it.className ?: "Kelompok B1"
                    )
                }
                studentDao.insertStudents(entities)
            }
        } catch (e: Exception) {
            // Offline fallback: keep local data
        }
    }
}
