package id.siapajar.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import id.siapajar.app.data.local.dao.*
import id.siapajar.app.data.local.entity.*

@Database(
    entities = [
        StudentEntity::class,
        AssessmentEntity::class,
        AttendanceEntity::class,
        SyncQueueEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SiapAjarDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun assessmentDao(): AssessmentDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun syncQueueDao(): SyncQueueDao

    companion object {
        @Volatile
        private var INSTANCE: SiapAjarDatabase? = null

        fun getDatabase(context: Context): SiapAjarDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SiapAjarDatabase::class.java,
                    "siapajar_offline.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
