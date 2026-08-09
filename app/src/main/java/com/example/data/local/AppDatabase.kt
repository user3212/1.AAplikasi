package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AttendanceRecord
import com.example.data.model.GradeRecord
import com.example.data.model.MasterClass
import com.example.data.model.MasterHalqah
import com.example.data.model.MasterSubject
import com.example.data.model.Santri
import com.example.data.model.TahfizRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Santri::class,
        TahfizRecord::class,
        GradeRecord::class,
        AttendanceRecord::class,
        MasterClass::class,
        MasterHalqah::class,
        MasterSubject::class,
        com.example.data.model.ModuleSetting::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun santriDao(): SantriDao
    abstract fun tahfizDao(): TahfizDao
    abstract fun gradeDao(): GradeDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun masterDao(): MasterDao
    abstract fun moduleSettingsDao(): ModuleSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pesantrenqu_offline.db"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Initial seed on database creation
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                seedInitialData(database)
                            }
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun seedInitialData(database: AppDatabase) {
            // Data dummy dihapus untuk rilis final
        }
    }
}
