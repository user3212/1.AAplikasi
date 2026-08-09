package com.example.data.repository

import com.example.data.local.AttendanceDao
import com.example.data.local.GradeDao
import com.example.data.local.MasterDao
import com.example.data.local.ModuleSettingsDao
import com.example.data.local.SantriDao
import com.example.data.local.TahfizDao
import com.example.data.model.AttendanceRecord
import com.example.data.model.GradeRecord
import com.example.data.model.MasterClass
import com.example.data.model.MasterHalqah
import com.example.data.model.MasterSubject
import com.example.data.model.ModuleSetting
import com.example.data.model.Santri
import com.example.data.model.TahfizRecord
import kotlinx.coroutines.flow.Flow

class PesantrenRepository(
    private val santriDao: SantriDao,
    private val tahfizDao: TahfizDao,
    private val gradeDao: GradeDao,
    private val attendanceDao: AttendanceDao,
    private val masterDao: MasterDao,
    private val settingsDao: ModuleSettingsDao
) {
    // Santri
    val allSantri: Flow<List<Santri>> = santriDao.getAllSantri()
    suspend fun insertSantri(santri: Santri): Long = santriDao.insertSantri(santri)
    suspend fun updateSantri(santri: Santri) = santriDao.updateSantri(santri)
    suspend fun deleteSantri(santri: Santri) = santriDao.deleteSantri(santri)
    suspend fun getSantriById(id: Long): Santri? = santriDao.getSantriById(id)

    // Tahfiz
    val allTahfizRecords: Flow<List<TahfizRecord>> = tahfizDao.getAllRecords()
    suspend fun insertTahfizRecord(record: TahfizRecord): Long = tahfizDao.insertRecord(record)
    suspend fun deleteTahfizRecord(record: TahfizRecord) = tahfizDao.deleteRecord(record)

    // Grades
    val allGrades: Flow<List<GradeRecord>> = gradeDao.getAllGrades()
    fun getGradesByCategory(category: String): Flow<List<GradeRecord>> = gradeDao.getGradesByCategory(category)
    suspend fun insertGrade(grade: GradeRecord): Long = gradeDao.insertGrade(grade)
    suspend fun insertAllGrades(list: List<GradeRecord>) = gradeDao.insertAllGrades(list)
    suspend fun deleteGrade(grade: GradeRecord) = gradeDao.deleteGrade(grade)

    // Attendance
    val allAttendance: Flow<List<AttendanceRecord>> = attendanceDao.getAllAttendance()
    suspend fun insertAttendance(attendance: AttendanceRecord): Long = attendanceDao.insertAttendance(attendance)
    suspend fun insertAllAttendance(list: List<AttendanceRecord>) = attendanceDao.insertAllAttendance(list)
    suspend fun deleteAttendance(attendance: AttendanceRecord) = attendanceDao.deleteAttendance(attendance)

    // Master Data
    val allClasses: Flow<List<MasterClass>> = masterDao.getAllClasses()
    suspend fun insertClass(masterClass: MasterClass) = masterDao.insertClass(masterClass)
    suspend fun deleteClass(masterClass: MasterClass) = masterDao.deleteClass(masterClass)

    val allHalqah: Flow<List<MasterHalqah>> = masterDao.getAllHalqah()
    suspend fun insertHalqah(masterHalqah: MasterHalqah) = masterDao.insertHalqah(masterHalqah)
    suspend fun deleteHalqah(masterHalqah: MasterHalqah) = masterDao.deleteHalqah(masterHalqah)

    val allSubjects: Flow<List<MasterSubject>> = masterDao.getAllSubjects()
    fun getSubjectsByCategory(category: String): Flow<List<MasterSubject>> = masterDao.getSubjectsByCategory(category)
    suspend fun insertSubject(subject: MasterSubject) = masterDao.insertSubject(subject)
    suspend fun deleteSubject(subject: MasterSubject) = masterDao.deleteSubject(subject)

    // Settings
    val allSettings: Flow<List<ModuleSetting>> = settingsDao.getAllSettings()
    suspend fun saveSetting(key: String, value: String) {
        settingsDao.insertSetting(ModuleSetting(key, value))
    }

    suspend fun updateSubjectNameAndRecords(category: String, oldName: String, newName: String) {
        gradeDao.updateMapelName(category, oldName, newName)
        masterDao.updateSubjectName(category, oldName, newName)
        attendanceDao.updateAttendanceSesi(oldName, "Mapel $oldName", newName)
    }

    suspend fun clearAllData() {
        santriDao.deleteAll()
        tahfizDao.deleteAll()
        gradeDao.deleteAll()
        attendanceDao.deleteAll()
        settingsDao.deleteAll()
    }
}
