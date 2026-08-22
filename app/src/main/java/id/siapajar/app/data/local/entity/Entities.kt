package id.siapajar.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey val id: String,
    val name: String,
    val nis: String,
    val photoUrl: String?,
    val classId: String,
    val className: String
)

@Entity(tableName = "assessments")
data class AssessmentEntity(
    @PrimaryKey val id: String,
    val studentIdsJson: String, // Stored as comma-separated or JSON list
    val studentNamesJson: String,
    val instrumentType: String,
    val photoPath: String?,
    val notes: String,
    val tpCode: String?,
    val tpTitle: String?,
    val syncStatus: String, // PENDING, SYNCING, SYNCED, FAILED
    val createdAt: Long
)

@Entity(tableName = "attendances")
data class AttendanceEntity(
    @PrimaryKey val id: String,
    val studentId: String,
    val studentName: String,
    val date: String,
    val status: String, // H, I, S, A
    val notes: String?,
    val syncStatus: String
)

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true) val queueId: Long = 0,
    val entityType: String, // ASSESSMENT, ATTENDANCE
    val entityId: String,
    val payloadJson: String,
    val retryCount: Int = 0,
    val status: String // PENDING, IN_PROGRESS, FAILED
)
