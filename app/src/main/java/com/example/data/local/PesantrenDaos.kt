package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AttendanceRecord
import com.example.data.model.GradeRecord
import com.example.data.model.MasterClass
import com.example.data.model.MasterHalqah
import com.example.data.model.MasterSubject
import com.example.data.model.Santri
import com.example.data.model.TahfizRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface SantriDao {
    @Query("SELECT * FROM santri ORDER BY nama ASC")
    fun getAllSantri(): Flow<List<Santri>>

    @Query("SELECT * FROM santri WHERE kelas = :kelas ORDER BY nama ASC")
    fun getSantriByKelas(kelas: String): Flow<List<Santri>>

    @Query("SELECT * FROM santri WHERE halqah = :halqah ORDER BY nama ASC")
    fun getSantriByHalqah(halqah: String): Flow<List<Santri>>

    @Query("SELECT * FROM santri WHERE id = :id LIMIT 1")
    suspend fun getSantriById(id: Long): Santri?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSantri(santri: Santri): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSantri(santriList: List<Santri>)

    @Update
    suspend fun updateSantri(santri: Santri)

    @Delete
    suspend fun deleteSantri(santri: Santri)

    @Query("DELETE FROM santri")
    suspend fun deleteAll()
}

@Dao
interface TahfizDao {
    @Query("SELECT * FROM tahfiz_records ORDER BY id DESC")
    fun getAllRecords(): Flow<List<TahfizRecord>>

    @Query("SELECT * FROM tahfiz_records WHERE santriId = :santriId ORDER BY id DESC")
    fun getRecordsForSantri(santriId: Long): Flow<List<TahfizRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: TahfizRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRecords(records: List<TahfizRecord>)

    @Delete
    suspend fun deleteRecord(record: TahfizRecord)

    @Query("DELETE FROM tahfiz_records")
    suspend fun deleteAll()
}

@Dao
interface GradeDao {
    @Query("SELECT * FROM grade_records ORDER BY id DESC")
    fun getAllGrades(): Flow<List<GradeRecord>>

    @Query("SELECT * FROM grade_records WHERE mapelCategory = :category ORDER BY id DESC")
    fun getGradesByCategory(category: String): Flow<List<GradeRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrade(grade: GradeRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllGrades(grades: List<GradeRecord>)

    @Query("UPDATE grade_records SET namaMapel = :newName WHERE mapelCategory = :category OR namaMapel = :oldName")
    suspend fun updateMapelName(category: String, oldName: String, newName: String)

    @Delete
    suspend fun deleteGrade(grade: GradeRecord)

    @Query("DELETE FROM grade_records")
    suspend fun deleteAll()
}

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance_records ORDER BY id DESC")
    fun getAllAttendance(): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE tanggal = :tanggal AND sesi = :sesi")
    fun getAttendanceByDateAndSession(tanggal: String, sesi: String): Flow<List<AttendanceRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAttendance(records: List<AttendanceRecord>)

    @Query("UPDATE attendance_records SET sesi = :newName WHERE sesi = :oldName OR sesi = :oldName2")
    suspend fun updateAttendanceSesi(oldName: String, oldName2: String, newName: String)

    @Delete
    suspend fun deleteAttendance(attendance: AttendanceRecord)

    @Query("DELETE FROM attendance_records")
    suspend fun deleteAll()
}

@Dao
interface MasterDao {
    @Query("SELECT * FROM master_class ORDER BY namaKelas ASC")
    fun getAllClasses(): Flow<List<MasterClass>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(masterClass: MasterClass)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllClasses(classes: List<MasterClass>)

    @Delete
    suspend fun deleteClass(masterClass: MasterClass)

    @Query("SELECT * FROM master_halqah ORDER BY namaHalqah ASC")
    fun getAllHalqah(): Flow<List<MasterHalqah>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHalqah(masterHalqah: MasterHalqah)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllHalqah(halqahs: List<MasterHalqah>)

    @Delete
    suspend fun deleteHalqah(masterHalqah: MasterHalqah)

    @Query("SELECT * FROM master_subject ORDER BY namaMapel ASC")
    fun getAllSubjects(): Flow<List<MasterSubject>>

    @Query("SELECT * FROM master_subject WHERE category = :category ORDER BY namaMapel ASC")
    fun getSubjectsByCategory(category: String): Flow<List<MasterSubject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: MasterSubject)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSubjects(subjects: List<MasterSubject>)

    @Query("UPDATE master_subject SET namaMapel = :newName WHERE category = :category OR namaMapel = :oldName")
    suspend fun updateSubjectName(category: String, oldName: String, newName: String)

    @Delete
    suspend fun deleteSubject(subject: MasterSubject)
}

@Dao
interface ModuleSettingsDao {
    @Query("SELECT * FROM module_settings")
    fun getAllSettings(): Flow<List<com.example.data.model.ModuleSetting>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: com.example.data.model.ModuleSetting)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSettings(settings: List<com.example.data.model.ModuleSetting>)

    @Query("DELETE FROM module_settings")
    suspend fun deleteAll()
}
