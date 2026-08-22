package id.siapajar.app.data.repository

import android.content.Context
import androidx.work.*
import id.siapajar.app.data.local.dao.*
import id.siapajar.app.data.local.entity.*
import id.siapajar.app.data.remote.SiapAjarApiService
import id.siapajar.app.domain.model.*
import id.siapajar.app.worker.SyncWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class AssessmentRepository(
    private val assessmentDao: AssessmentDao,
    private val apiService: SiapAjarApiService,
    private val context: Context
) {
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
    ) {
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
        // 1. Save optimistic local write to SQLite
        assessmentDao.insertAssessment(entity)

        // 2. Trigger background sync via WorkManager
        triggerSyncWorker()
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
        instrumentType = InstrumentType.valueOf(instrumentType),
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
    private val apiService: SiapAjarApiService,
    private val context: Context
) {
    fun getAttendancesByDate(date: String): Flow<List<Attendance>> =
        attendanceDao.getAttendancesByDate(date).map { entities ->
            entities.map { it.toDomain() }
        }

    suspend fun saveAttendances(items: List<Attendance>) {
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
