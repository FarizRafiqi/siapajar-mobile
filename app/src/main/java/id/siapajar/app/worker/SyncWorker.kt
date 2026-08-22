package id.siapajar.app.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import id.siapajar.app.data.local.SiapAjarDatabase
import id.siapajar.app.data.remote.ApiClient
import id.siapajar.app.domain.model.SyncStatus
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val db = SiapAjarDatabase.getDatabase(applicationContext)
        val assessmentDao = db.assessmentDao()
        val apiService = ApiClient.getApiService(applicationContext)

        return try {
            val pendingAssessments = assessmentDao.getPendingAssessments()
            for (assessment in pendingAssessments) {
                try {
                    val classIdPart = "1".toRequestBody("text/plain".toMediaTypeOrNull())
                    val studentIdsPart = assessment.studentIdsJson.toRequestBody("text/plain".toMediaTypeOrNull())
                    val instrumentPart = assessment.instrumentType.toRequestBody("text/plain".toMediaTypeOrNull())
                    val notesPart = assessment.notes.toRequestBody("text/plain".toMediaTypeOrNull())

                    var photoPart: MultipartBody.Part? = null
                    if (!assessment.photoPath.isNullOrBlank()) {
                        val file = File(assessment.photoPath)
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
                        assessmentDao.updateSyncStatus(assessment.id, SyncStatus.SYNCED.name)
                    }
                } catch (_: Exception) {
                    // Retry individual upload next time
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
