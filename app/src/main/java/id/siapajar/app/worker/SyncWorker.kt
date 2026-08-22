package id.siapajar.app.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import id.siapajar.app.data.local.SiapAjarDatabase
import id.siapajar.app.domain.model.SyncStatus

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val db = SiapAjarDatabase.getDatabase(applicationContext)
        val assessmentDao = db.assessmentDao()

        return try {
            val pendingAssessments = assessmentDao.getPendingAssessments()
            for (assessment in pendingAssessments) {
                // Perform upload to AdonisJS Backend API
                // On success:
                assessmentDao.updateSyncStatus(assessment.id, SyncStatus.SYNCED.name)
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
