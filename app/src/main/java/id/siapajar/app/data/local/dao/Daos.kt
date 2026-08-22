package id.siapajar.app.data.local.dao

import androidx.room.*
import id.siapajar.app.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query("SELECT * FROM students WHERE classId = :classId ORDER BY name ASC")
    fun getStudentsByClass(classId: String): Flow<List<StudentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<StudentEntity>)

    @Query("SELECT * FROM students WHERE id = :id")
    suspend fun getStudentById(id: String): StudentEntity?
}

@Dao
interface AssessmentDao {
    @Query("SELECT * FROM assessments ORDER BY createdAt DESC")
    fun getAllAssessments(): Flow<List<AssessmentEntity>>

    @Query("SELECT * FROM assessments WHERE studentIdsJson LIKE '%' || :studentId || '%' ORDER BY createdAt DESC")
    fun getAssessmentsForStudent(studentId: String): Flow<List<AssessmentEntity>>

    @Query("SELECT * FROM assessments WHERE syncStatus = 'PENDING'")
    suspend fun getPendingAssessments(): List<AssessmentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessment(assessment: AssessmentEntity)

    @Query("UPDATE assessments SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: String)
}

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendances WHERE date = :date ORDER BY studentName ASC")
    fun getAttendancesByDate(date: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendances WHERE syncStatus = 'PENDING'")
    suspend fun getPendingAttendances(): List<AttendanceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendances(attendances: List<AttendanceEntity>)

    @Query("UPDATE attendances SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: String)
}

@Dao
interface SyncQueueDao {
    @Query("SELECT * FROM sync_queue WHERE status = 'PENDING' ORDER BY queueId ASC")
    suspend fun getPendingQueue(): List<SyncQueueEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueue(item: SyncQueueEntity)

    @Delete
    suspend fun dequeue(item: SyncQueueEntity)
}
