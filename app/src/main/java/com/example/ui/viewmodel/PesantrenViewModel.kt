package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.ui.util.AlarmScheduler
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AttendanceRecord
import com.example.data.model.GradeRecord
import com.example.data.model.MasterClass
import com.example.data.model.MasterHalqah
import com.example.data.model.MasterSubject
import com.example.data.model.Santri
import com.example.data.model.TahfizRecord
import com.example.data.repository.PesantrenRepository
import com.example.ui.components.NavItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject


data class JamMengajar(val id: String, var jamKe: String, var waktuMulai: String, var waktuSelesai: String)
data class HariJadwal(val id: String, var hari: String, var isExpanded: Boolean, val jamList: MutableList<JamMengajar>)

data class CustomSubjectNavState(
    val subjectId: Long,
    val subjectName: String,
    val subType: String // "ROMBEL", "ABSENSI", "PENILAIAN_HARIAN", "PENILAIAN_UTS", "PENILAIAN_PAS"
)

class PesantrenViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("pesantren_prefs", android.content.Context.MODE_PRIVATE)
    fun getConfigJuz(): String = prefs.getString("config_juz", "Juz 30") ?: "Juz 30"
    fun setConfigJuz(juz: String) { prefs.edit().putString("config_juz", juz).apply() }
    fun getSantriJuz(santriId: Long): String = prefs.getString("santri_juz_$santriId", getConfigJuz()) ?: getConfigJuz()
    fun setSantriJuz(santriId: Long, juz: String) { prefs.edit().putString("santri_juz_$santriId", juz).apply() }
    fun resetSantriJuz(santriId: Long) { prefs.edit().remove("santri_juz_$santriId").apply() }
    fun hasCustomJuz(santriId: Long): Boolean = prefs.contains("santri_juz_$santriId")
    private val database = AppDatabase.getDatabase(application)
    private val repository = PesantrenRepository(
        santriDao = database.santriDao(),
        tahfizDao = database.tahfizDao(),
        gradeDao = database.gradeDao(),
        attendanceDao = database.attendanceDao(),
        masterDao = database.masterDao(),
        settingsDao = database.moduleSettingsDao()
    )




    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("is_dark_mode", false))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        val newVal = !_isDarkMode.value
        _isDarkMode.value = newVal
        prefs.edit().putBoolean("is_dark_mode", newVal).apply()
    }

    // PROFILE LOGIC
    private val _namaGuru = MutableStateFlow(prefs.getString("nama_guru", "") ?: "")
    val namaGuru: StateFlow<String> = _namaGuru.asStateFlow()
    
    private val _namaSekolah = MutableStateFlow(prefs.getString("nama_sekolah", "") ?: "")
    val namaSekolah: StateFlow<String> = _namaSekolah.asStateFlow()
    
    private val _alamatSekolah = MutableStateFlow(prefs.getString("alamat_sekolah", "") ?: "")
    val alamatSekolah: StateFlow<String> = _alamatSekolah.asStateFlow()
    
    private val _jenisKelamin = MutableStateFlow(prefs.getString("jenis_kelamin", "Laki-laki") ?: "Laki-laki")
    val jenisKelamin: StateFlow<String> = _jenisKelamin.asStateFlow()
    
    private val _profilBase64 = MutableStateFlow(prefs.getString("profil_base64", "") ?: "")
    val profilBase64: StateFlow<String> = _profilBase64.asStateFlow()

    fun updateProfile(nama: String, sekolah: String, alamat: String, jk: String, base64: String) {
        _namaGuru.value = nama
        _namaSekolah.value = sekolah
        _alamatSekolah.value = alamat
        _jenisKelamin.value = jk
        _profilBase64.value = base64
        prefs.edit()
            .putString("nama_guru", nama)
            .putString("nama_sekolah", sekolah)
            .putString("alamat_sekolah", alamat)
            .putString("jenis_kelamin", jk)
            .putString("profil_base64", base64)
            .apply()
        showToast("Profil Berhasil Diperbarui")
    }
    
    // JADWAL CONFIG
    private val _jadwalSound = MutableStateFlow(prefs.getString("jadwal_sound", "Sound 1") ?: "Sound 1")
    val jadwalSound: StateFlow<String> = _jadwalSound.asStateFlow()
    
    private val _jadwalRepetition = MutableStateFlow(prefs.getInt("jadwal_repetition", 1))
    val jadwalRepetition: StateFlow<Int> = _jadwalRepetition.asStateFlow()
    
    fun updateJadwalConfig(sound: String, repetition: Int) {
        _jadwalSound.value = sound
        _jadwalRepetition.value = repetition
        prefs.edit()
            .putString("jadwal_sound", sound)
            .putInt("jadwal_repetition", repetition)
            .apply()
        AlarmScheduler.scheduleAlarms(getApplication(), _jadwalList.value, sound, repetition)
    }

    // JADWAL LOGIC
    private val _jadwalList = MutableStateFlow<List<HariJadwal>>(emptyList())
    val jadwalList: StateFlow<List<HariJadwal>> = _jadwalList.asStateFlow()

    init {
        loadJadwal()
        startAlarmChecker()
    }

    fun loadJadwal() {
        val jsonString = prefs.getString("jadwal_data", "[]") ?: "[]"
        try {
            val jsonArray = org.json.JSONArray(jsonString)
            val list = mutableListOf<HariJadwal>()
            for (i in 0 until jsonArray.length()) {
                val dayObj = jsonArray.getJSONObject(i)
                val jamArr = dayObj.getJSONArray("jamList")
                val jamList = mutableListOf<JamMengajar>()
                for (j in 0 until jamArr.length()) {
                    val jamObj = jamArr.getJSONObject(j)
                    jamList.add(
                        JamMengajar(
                            id = jamObj.getString("id"),
                            jamKe = jamObj.getString("jamKe"),
                            waktuMulai = jamObj.getString("waktuMulai"),
                            waktuSelesai = jamObj.getString("waktuSelesai")
                        )
                    )
                }
                list.add(
                    HariJadwal(
                        id = dayObj.getString("id"),
                        hari = dayObj.getString("hari"),
                        isExpanded = dayObj.getBoolean("isExpanded"),
                        jamList = jamList
                    )
                )
            }
            _jadwalList.value = list
            AlarmScheduler.scheduleAlarms(getApplication(), list, _jadwalSound.value, _jadwalRepetition.value)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveJadwal(list: List<HariJadwal>) {
        _jadwalList.value = list
        val jsonArray = org.json.JSONArray()
        list.forEach { day ->
            val dayObj = org.json.JSONObject()
            dayObj.put("id", day.id)
            dayObj.put("hari", day.hari)
            dayObj.put("isExpanded", day.isExpanded)
            val jamArr = org.json.JSONArray()
            day.jamList.forEach { jam ->
                val jamObj = org.json.JSONObject()
                jamObj.put("id", jam.id)
                jamObj.put("jamKe", jam.jamKe)
                jamObj.put("waktuMulai", jam.waktuMulai)
                jamObj.put("waktuSelesai", jam.waktuSelesai)
                jamArr.put(jamObj)
            }
            dayObj.put("jamList", jamArr)
            jsonArray.put(dayObj)
        }
        prefs.edit().putString("jadwal_data", jsonArray.toString()).apply()
        AlarmScheduler.scheduleAlarms(getApplication(), list, _jadwalSound.value, _jadwalRepetition.value)
    }
    
    private val _alarmPopup = MutableStateFlow<String?>(null)
    val alarmPopup: StateFlow<String?> = _alarmPopup.asStateFlow()

    fun clearAlarm() {
        _alarmPopup.value = null
    }

    private fun startAlarmChecker() {
        viewModelScope.launch {
            while (true) {
                checkAlarm()
                kotlinx.coroutines.delay(60000) // check every minute
            }
        }
    }

    private fun checkAlarm() {
        val now = java.time.LocalDateTime.now()
        val dayOfWeek = now.dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale("id", "ID"))
        val currentHari = _jadwalList.value.find { it.hari.equals(dayOfWeek, ignoreCase = true) }
        
        currentHari?.jamList?.forEach { jam ->
            if (jam.waktuMulai.isNotBlank()) {
                try {
                    val parts = jam.waktuMulai.split(":")
                    if (parts.size == 2) {
                        val h = parts[0].toIntOrNull() ?: 0
                        val m = parts[1].toIntOrNull() ?: 0
                        
                        val startMinutes = h * 60 + m
                        val nowMinutes = now.hour * 60 + now.minute
                        
                        // 10 minutes before
                        if (startMinutes - nowMinutes == 10) {
                            // Trigger notification
                            val text = "10 menit menuju ${jam.jamKe.lowercase()} (${jam.waktuMulai} - ${jam.waktuSelesai})"
                            _alarmPopup.value = text
                            // Also optionally show a regular toast
                            showToast(text)
                        }
                    }
                } catch (e: Exception) {}
            }
        }
    }

    // Navigation & Drawer State
    private val _selectedNavItem = MutableStateFlow(NavItem.BERANDA)
    val selectedNavItem: StateFlow<NavItem> = _selectedNavItem.asStateFlow()

    private val _activeCustomSubjectNav = MutableStateFlow<CustomSubjectNavState?>(null)
    val activeCustomSubjectNav: StateFlow<CustomSubjectNavState?> = _activeCustomSubjectNav.asStateFlow()

    private val _isDrawerOpen = MutableStateFlow(false)
    val isDrawerOpen: StateFlow<Boolean> = _isDrawerOpen.asStateFlow()

    // Module Visibility & Dynamic Subject Names State
    private val _mapel1Name = MutableStateFlow("Mapel SKI")
    val mapel1Name: StateFlow<String> = _mapel1Name.asStateFlow()

    private val _mapel2Name = MutableStateFlow("Mapel Informatika")
    val mapel2Name: StateFlow<String> = _mapel2Name.asStateFlow()

    private val _isTahfizVisible = MutableStateFlow(true)
    val isTahfizVisible: StateFlow<Boolean> = _isTahfizVisible.asStateFlow()

    private val _isMapel1Visible = MutableStateFlow(true)
    val isMapel1Visible: StateFlow<Boolean> = _isMapel1Visible.asStateFlow()

    private val _isMapel2Visible = MutableStateFlow(true)
    val isMapel2Visible: StateFlow<Boolean> = _isMapel2Visible.asStateFlow()

    // Toast State

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    private val _isLoadingOverlayVisible = MutableStateFlow(false)
    val isLoadingOverlayVisible: StateFlow<Boolean> = _isLoadingOverlayVisible

    private val _loadingOverlayMessage = MutableStateFlow("")
    val loadingOverlayMessage: StateFlow<String> = _loadingOverlayMessage

    private val _importResultLog = MutableStateFlow<String?>(null)
    val importResultLog: StateFlow<String?> = _importResultLog.asStateFlow()

    fun clearImportResultLog() {
        _importResultLog.value = null
    }

    fun showLoadingOverlay(message: String, durationMs: Long = 2000L) {
        _loadingOverlayMessage.value = message
        _isLoadingOverlayVisible.value = true
        viewModelScope.launch {
            kotlinx.coroutines.delay(durationMs)
            _isLoadingOverlayVisible.value = false
            showToast(message.replace("Sedang ", "").replace("...", " berhasil"))
        }
    }

    init {
        viewModelScope.launch {
            repository.allSettings.collect { settingsList ->
                settingsList.forEach { setting ->
                    when (setting.key) {
                        "mapel1_name" -> _mapel1Name.value = setting.value
                        "mapel2_name" -> _mapel2Name.value = setting.value
                        "tahfiz_visible" -> _isTahfizVisible.value = setting.value.toBooleanStrictOrNull() ?: true
                        "mapel1_visible" -> _isMapel1Visible.value = setting.value.toBooleanStrictOrNull() ?: true
                        "mapel2_visible" -> _isMapel2Visible.value = setting.value.toBooleanStrictOrNull() ?: true
                    }
                }
            }
        }
    }

    // Database Streams
    val santriList: StateFlow<List<Santri>> = repository.allSantri.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val tahfizRecords: StateFlow<List<TahfizRecord>> = repository.allTahfizRecords.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val gradeRecords: StateFlow<List<GradeRecord>> = repository.allGrades.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val attendanceRecords: StateFlow<List<AttendanceRecord>> = repository.allAttendance.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val masterClasses: StateFlow<List<MasterClass>> = repository.allClasses.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val masterHalqahs: StateFlow<List<MasterHalqah>> = repository.allHalqah.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val masterSubjects: StateFlow<List<MasterSubject>> = repository.allSubjects.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun selectNavItem(item: NavItem) {
        _selectedNavItem.value = item
        _activeCustomSubjectNav.value = null
    }

    fun selectCustomSubjectNav(subjectId: Long, subjectName: String, subType: String) {
        _activeCustomSubjectNav.value = CustomSubjectNavState(subjectId, subjectName, subType)
    }

    fun applySubjectNames(newMapel1: String, newMapel2: String) {
        viewModelScope.launch {
            val old1 = _mapel1Name.value
            val old2 = _mapel2Name.value

            if (newMapel1.isNotBlank() && newMapel1 != old1) {
                repository.updateSubjectNameAndRecords("MAPEL1", old1, newMapel1)
                repository.saveSetting("mapel1_name", newMapel1)
                _mapel1Name.value = newMapel1
            }

            if (newMapel2.isNotBlank() && newMapel2 != old2) {
                repository.updateSubjectNameAndRecords("MAPEL2", old2, newMapel2)
                repository.saveSetting("mapel2_name", newMapel2)
                _mapel2Name.value = newMapel2
            }

            showToast("Nama mata pelajaran berhasil diperbarui!")
        }
    }

    fun toggleTahfizVisibility(visible: Boolean) {
        viewModelScope.launch {
            _isTahfizVisible.value = visible
            repository.saveSetting("tahfiz_visible", visible.toString())
        }
    }

    fun toggleMapel1Visibility(visible: Boolean) {
        viewModelScope.launch {
            _isMapel1Visible.value = visible
            repository.saveSetting("mapel1_visible", visible.toString())
        }
    }

    fun toggleMapel2Visibility(visible: Boolean) {
        viewModelScope.launch {
            _isMapel2Visible.value = visible
            repository.saveSetting("mapel2_visible", visible.toString())
        }
    }

    fun toggleCustomSubjectVisibility(subject: MasterSubject, visible: Boolean) {
        viewModelScope.launch {
            val updated = subject.copy(isVisible = visible)
            repository.insertSubject(updated)
        }
    }

    fun addCustomSubjectModule(namaMapel: String, pengampu: String = "Ustaz Pengampu") {
        viewModelScope.launch {
            if (namaMapel.isNotBlank()) {
                val formattedName = if (namaMapel.startsWith("Mapel", ignoreCase = true)) namaMapel else "Mapel $namaMapel"
                val newSubject = MasterSubject(
                    namaMapel = formattedName,
                    category = "CUSTOM",
                    pengampu = pengampu,
                    isVisible = true
                )
                repository.insertSubject(newSubject)
                showToast("Modul $formattedName berhasil ditambahkan ke sidebar!")
            }
        }
    }

    fun toggleDrawer(open: Boolean? = null) {
        _isDrawerOpen.value = open ?: !_isDrawerOpen.value
    }

    
    fun clearDatabase() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }

    fun showToast(message: String) {
        _toastMessage.value = message
        viewModelScope.launch {
            kotlinx.coroutines.delay(3000)
            if (_toastMessage.value == message) {
                _toastMessage.value = null
            }
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    // TAHFIZ CRUD
    fun addTahfizRecord(
        santri: Santri,
        tanggal: String,
        jenisSetoran: String,
        surahJuz: String,
        ayatMulai: String,
        ayatSelesai: String,
        nilai: String,
        pengampu: String,
        catatan: String
    ) {
        viewModelScope.launch {
            val record = TahfizRecord(
                santriId = santri.id,
                santriNama = santri.nama,
                tanggal = tanggal,
                jenisSetoran = jenisSetoran,
                surahJuz = surahJuz,
                ayatMulai = ayatMulai,
                ayatSelesai = ayatSelesai,
                nilai = nilai,
                pengampu = pengampu,
                catatan = catatan
            )
            repository.insertTahfizRecord(record)
            showToast("Setoran Tahfiz ${santri.nama} berhasil disimpan!")
        }
    }

    fun deleteTahfizRecord(record: TahfizRecord) {
        viewModelScope.launch {
            repository.deleteTahfizRecord(record)
            showToast("Catatan setoran telah dihapus.")
        }
    }

    // GRADE CRUD
    fun addGradeRecord(
        santri: Santri,
        mapelCategory: String, // "MAPEL1" or "MAPEL2"
        namaMapel: String,
        tanggal: String,
        jenisUjian: String,
        nilai: Double,
        pengampu: String,
        catatan: String
    ) {
        viewModelScope.launch {
            val grade = GradeRecord(
                santriId = santri.id,
                santriNama = santri.nama,
                mapelCategory = mapelCategory,
                namaMapel = namaMapel,
                tanggal = tanggal,
                jenisUjian = jenisUjian,
                nilai = nilai,
                pengampu = pengampu,
                catatan = catatan
            )
            repository.insertGrade(grade)
            val moduleLabel = if (mapelCategory == "MAPEL1") "Mapel 1" else "Mapel 2"
            showToast("Nilai $moduleLabel untuk ${santri.nama} berhasil disimpan!")
        }
    }

    fun deleteGradeRecord(grade: GradeRecord) {
        viewModelScope.launch {
            repository.deleteGrade(grade)
            showToast("Nilai berhasil dihapus.")
        }
    }

    fun saveBatchGrades(records: List<GradeRecord>) {
        viewModelScope.launch {
            repository.insertAllGrades(records)
            showToast("Nilai ${records.size} siswa berhasil disimpan!")
        }
    }

    // ATTENDANCE CRUD
    fun saveAttendance(
        santri: Santri,
        tanggal: String,
        sesi: String,
        status: String,
        keterangan: String = ""
    ) {
        viewModelScope.launch {
            val record = AttendanceRecord(
                santriId = santri.id,
                santriNama = santri.nama,
                tanggal = tanggal,
                sesi = sesi,
                status = status,
                keterangan = keterangan
            )
            repository.insertAttendance(record)
            showToast("Presensi ${santri.nama} set ($status)")
        }
    }

    fun saveBatchAttendance(records: List<AttendanceRecord>) {
        viewModelScope.launch {
            repository.insertAllAttendance(records)
            showToast("Presensi ${records.size} santri berhasil diperbarui!")
        }
    }

    // SANTRI CRUD
    fun saveSantri(
        id: Long = 0,
        nis: String,
        nama: String,
        gender: String,
        kelas: String,
        halqah: String,
        status: String,
        catatan: String
    ) {
        viewModelScope.launch {
            val santri = Santri(
                id = id,
                nis = nis,
                nama = nama,
                gender = gender,
                kelas = kelas,
                halqah = halqah,
                status = status,
                catatan = catatan
            )
            repository.insertSantri(santri)
            showToast("Data Santri ${santri.nama} berhasil disimpan!")
        }
    }

    fun deleteSantri(santri: Santri) {
        viewModelScope.launch {
            repository.deleteSantri(santri)
            showToast("Data santri ${santri.nama} dihapus.")
        }
    }

        fun importSantriBatch(rawItems: List<Pair<String, String>>) {
        viewModelScope.launch {
            var count = 0
            val existing = repository.allSantri.first()
            rawItems.forEach { (nama, kelas) ->
                if (nama.isNotBlank() && kelas.isNotBlank()) {
                    val formattedKelas = kelas.trim()
                    
                    val exists = existing.any { it.halqah == "UMUM" && it.nama.equals(nama.trim(), ignoreCase = true) && it.kelas.equals(formattedKelas, ignoreCase = true) }
                    
                    if (!exists) {
                        val nextNum = (existing.count { it.halqah == "UMUM" && it.kelas.equals(formattedKelas, ignoreCase = true) } + count) + 1
                        
                        val santri = Santri(
                            nis = "2026${(1000..9999).random()}",
                            nama = nama.trim(),
                            gender = "L",
                            kelas = formattedKelas,
                            halqah = "UMUM",
                            status = "Aktif",
                            catatan = nextNum.toString()
                        )
                        repository.insertSantri(santri)
                        
                        val existingClasses = masterClasses.value.map { it.namaKelas }
                        if (!existingClasses.contains(formattedKelas)) {
                            repository.insertClass(MasterClass(namaKelas = formattedKelas, waliKelas = "Wali Kelas"))
                        }
                        
                        count++
                    }
                }
            }
            showToast("Berhasil impor $count santri baru. Rombel otomatis diperbarui.")
        }
    }

    // MASTER DATA CRUD
    fun addClass(namaKelas: String, waliKelas: String) {
        viewModelScope.launch {
            repository.insertClass(MasterClass(namaKelas = namaKelas, waliKelas = waliKelas))
            showToast("Kelas $namaKelas ditambahkan.")
        }
    }

    fun deleteClass(masterClass: MasterClass) {
        viewModelScope.launch {
            repository.deleteClass(masterClass)
            showToast("Kelas ${masterClass.namaKelas} dihapus.")
        }
    }

    fun addHalqah(namaHalqah: String, ustaz: String) {
        viewModelScope.launch {
            repository.insertHalqah(MasterHalqah(namaHalqah = namaHalqah, ustaz = ustaz))
            showToast("Halqah $namaHalqah ditambahkan.")
        }
    }

    fun deleteHalqah(masterHalqah: MasterHalqah) {
        viewModelScope.launch {
            repository.deleteHalqah(masterHalqah)
            showToast("Halqah ${masterHalqah.namaHalqah} dihapus.")
        }
    }

    fun addSubject(namaMapel: String, category: String, pengampu: String) {
        viewModelScope.launch {
            repository.insertSubject(MasterSubject(namaMapel = namaMapel, category = category, pengampu = pengampu))
            showToast("Mata Pelajaran $namaMapel ditambahkan.")
        }
    }

    fun deleteSubject(subject: MasterSubject) {
        viewModelScope.launch {
            repository.deleteSubject(subject)
            showToast("Mata pelajaran ${subject.namaMapel} dihapus.")
        }
    }

    // EXPORT / IMPORT BACKUP JSON
    fun exportBackupJson(): String {
        val root = JSONObject()
        val currentSantri = santriList.value
        val currentTahfiz = tahfizRecords.value
        val currentGrades = gradeRecords.value
        val currentAttendance = attendanceRecords.value

        val santriArray = JSONArray()
        currentSantri.forEach { s ->
            val obj = JSONObject()
            obj.put("id", s.id)
            obj.put("nis", s.nis)
            obj.put("nama", s.nama)
            obj.put("gender", s.gender)
            obj.put("kelas", s.kelas)
            obj.put("halqah", s.halqah)
            obj.put("status", s.status)
            obj.put("catatan", s.catatan)
            santriArray.put(obj)
        }
        root.put("santri", santriArray)

        val tahfizArray = JSONArray()
        currentTahfiz.forEach { t ->
            val obj = JSONObject()
            obj.put("id", t.id)
            obj.put("santriId", t.santriId)
            obj.put("santriNama", t.santriNama)
            obj.put("tanggal", t.tanggal)
            obj.put("jenisSetoran", t.jenisSetoran)
            obj.put("surahJuz", t.surahJuz)
            obj.put("ayatMulai", t.ayatMulai)
            obj.put("ayatSelesai", t.ayatSelesai)
            obj.put("nilai", t.nilai)
            obj.put("pengampu", t.pengampu)
            obj.put("catatan", t.catatan)
            tahfizArray.put(obj)
        }
        root.put("tahfiz", tahfizArray)

        val gradeArray = JSONArray()
        currentGrades.forEach { g ->
            val obj = JSONObject()
            obj.put("id", g.id)
            obj.put("santriId", g.santriId)
            obj.put("santriNama", g.santriNama)
            obj.put("mapelCategory", g.mapelCategory)
            obj.put("namaMapel", g.namaMapel)
            obj.put("tanggal", g.tanggal)
            obj.put("jenisUjian", g.jenisUjian)
            obj.put("nilai", g.nilai)
            obj.put("pengampu", g.pengampu)
            obj.put("catatan", g.catatan)
            gradeArray.put(obj)
        }
        root.put("grades", gradeArray)
        root.put("pbm", gradeArray) // Added for compatibility with version 1.3.0

        val attendanceArray = JSONArray()
        currentAttendance.forEach { a ->
            val obj = JSONObject()
            obj.put("id", a.id)
            obj.put("santriId", a.santriId)
            obj.put("santriNama", a.santriNama)
            obj.put("tanggal", a.tanggal)
            obj.put("sesi", a.sesi)
            obj.put("status", a.status)
            obj.put("keterangan", a.keterangan)
            attendanceArray.put(obj)
        }
        root.put("attendance", attendanceArray)
        // PBM bisa di mix dengan attendance, tapi untuk simplifikasi simpan pbm = gradeArray

        return root.toString(2)
    }

    

    
    fun importBackupJson(jsonString: String) {
        viewModelScope.launch {
            val log = java.lang.StringBuilder()
            log.append("Memulai proses impor database...\n\n")
            try {
                val root = org.json.JSONObject(jsonString)
                
                var santriCount = 0
                var tahfizCount = 0
                var pbmGradeCount = 0
                var pbmAttendanceCount = 0

                val existingSantri = repository.allSantri.first()
                val santriMapByNameAndClass = existingSantri.associateBy { "${it.nama}_${it.kelas}_${it.halqah}" }.toMutableMap()

                                val existingSubjects = repository.allSubjects.first()
                suspend fun getOrCreateSantri(nama: String, kelas: String, mapelCategory: String): Long {
                    val key = "${nama}_${kelas}_${mapelCategory}"
                    val existing = santriMapByNameAndClass[key]
                    if (existing != null) {
                        return existing.id
                    }
                    val newSantri = com.example.data.model.Santri(nis = "", nama = nama, gender = "L", kelas = kelas, halqah = mapelCategory, status = "Aktif")
                    val newId = repository.insertSantri(newSantri)
                    santriMapByNameAndClass[key] = newSantri.copy(id = newId)
                    
                    if (mapelCategory == "UMUM") {
                        val existingClasses = masterClasses.value.map { it.namaKelas }
                        if (!existingClasses.contains(kelas)) {
                            repository.insertClass(MasterClass(namaKelas = kelas, waliKelas = "Wali Kelas"))
                        }
                    }
                    
                    santriCount++
                    return newId
                }

                fun resolveMapelCategory(mapel: String): String? {
                    if (mapel.equals("Mapel SKI", ignoreCase = true)) return "MAPEL1"
                    if (mapel.equals("Mapel Informatika", ignoreCase = true)) return "MAPEL2"
                    val found = existingSubjects.find { it.namaMapel.equals(mapel, ignoreCase = true) }
                    if (found != null) {
                        if (found.category == "CUSTOM") return "CUSTOM_${found.namaMapel}"
                        return found.category
                    }
                    return null
                }

                if (root.has("tahfiz")) {
                    val tahfizArray = root.getJSONArray("tahfiz")
                    for (i in 0 until tahfizArray.length()) {
                        val obj = tahfizArray.getJSONObject(i)
                        val nama = obj.optString("nama", "Fulan")
                        val kelas = obj.optString("kelas", "")
                        
                        val sId = getOrCreateSantri(nama, kelas, "UMUM")
                        
                        val detail = obj.optJSONObject("detail")
                        if (detail != null) {
                            val hafalan = detail.optJSONObject("hafalan")
                            if (hafalan != null) {
                                val keys = hafalan.keys()
                                while (keys.hasNext()) {
                                    val surah = keys.next()
                                    val ayat = hafalan.optInt(surah, 1)
                                    val record = com.example.data.model.TahfizRecord(
                                        santriId = sId,
                                        santriNama = nama,
                                        tanggal = "2026-08-08",
                                        jenisSetoran = "Ziyadah",
                                        surahJuz = surah,
                                        ayatMulai = "1",
                                        ayatSelesai = ayat.toString(),
                                        nilai = "Mumtaz (A)",
                                        pengampu = "Sistem Import"
                                    )
                                    repository.insertTahfizRecord(record)
                                    tahfizCount++
                                }
                            }
                            
                            val absensi = detail.optJSONObject("absensi")
                            if (absensi != null) {
                                val dates = absensi.keys()
                                while (dates.hasNext()) {
                                    val date = dates.next()
                                    val absObj = absensi.optJSONObject(date)
                                    val status = absObj?.optString("status", "H") ?: "H"
                                    val ket = absObj?.optString("keterangan", "") ?: ""
                                    
                                    val attRecord = com.example.data.model.AttendanceRecord(
                                        santriId = sId,
                                        santriNama = nama,
                                        tanggal = date,
                                        sesi = "KBM Tahfiz",
                                        status = status,
                                        keterangan = ket
                                    )
                                    repository.insertAttendance(attRecord)
                                    pbmAttendanceCount++
                                }
                            }
                        }
                    }
                    log.append("- Data Tahfiz berhasil diimpor: $tahfizCount record.\n")
                }
                
                if (root.has("pbm")) {
                    val pbmArray = root.getJSONArray("pbm")
                    for (i in 0 until pbmArray.length()) {
                        val obj = pbmArray.getJSONObject(i)
                        val nama = obj.optString("nama", "Fulan")
                        val kelas = obj.optString("rombel", "")
                                                val mapel = obj.optString("mapel", "Mapel")
                        
                        val category = resolveMapelCategory(mapel)
                        if (category == null) {
                            log.append("❌ GAGAL: Mapel '$mapel' tidak ditemukan untuk santri $nama. Silakan buat Mapel '$mapel' terlebih dahulu di menu Pengaturan.\n")
                            continue
                        }
                        
                        val sId = getOrCreateSantri(nama, kelas, category)
                        
                        val detail = obj.optJSONObject("detail")
                        if (detail != null) {
                            val penilaian = detail.optJSONObject("penilaian")
                            if (penilaian != null) {
                                val pKeys = penilaian.keys()
                                while (pKeys.hasNext()) {
                                    val key = pKeys.next()
                                    val pObj = penilaian.optJSONObject(key)
                                    if (pObj != null) {
                                        val materi = pObj.optString("materi", "")
                                        val tanggal = pObj.optString("tanggal", "2026-08-08")
                                        val nilai = pObj.optString("nilai", "0")
                                        val ket = pObj.optString("keterangan", "")
                                        
                                        val record = com.example.data.model.GradeRecord(
                                            santriId = sId,
                                            santriNama = nama,
                                            mapelCategory = category,
                                            namaMapel = mapel,
                                            tanggal = tanggal,
                                            jenisUjian = materi,
                                            nilai = nilai.toDoubleOrNull() ?: 0.0,
                                            pengampu = "Sistem Import",
                                            catatan = ket
                                        )
                                        repository.insertGrade(record)
                                        pbmGradeCount++
                                    }
                                }
                            }
                            
                            val absensi = detail.optJSONObject("absensi")
                            if (absensi != null) {
                                val dates = absensi.keys()
                                while (dates.hasNext()) {
                                    val date = dates.next()
                                    val absObj = absensi.optJSONObject(date)
                                    val status = absObj?.optString("status", "H") ?: "H"
                                    val ket = absObj?.optString("keterangan", "") ?: ""
                                    
                                    val attRecord = com.example.data.model.AttendanceRecord(
                                        santriId = sId,
                                        santriNama = nama,
                                        tanggal = date,
                                        sesi = mapel,
                                        status = status,
                                        keterangan = ket
                                    )
                                    repository.insertAttendance(attRecord)
                                    pbmAttendanceCount++
                                }
                            }
                        }
                    }
                    log.append("- Data PBM Nilai berhasil diimpor: $pbmGradeCount record.\n")
                    log.append("- Data PBM Absensi berhasil diimpor: $pbmAttendanceCount record.\n")
                }
                
                if (santriCount > 0) {
                    log.append("- Data Santri baru dibuat: $santriCount siswa.\n")
                }
                
                log.append("\nStatus: SUCCESS\nData berhasil disinkronkan ke dalam sistem!")
            } catch (e: Exception) {
                e.printStackTrace()
                log.append("\nError Parsing File:\n${e.message}\n\nPastikan format JSON sesuai dengan struktur yang didukung aplikasi.")
            }
            
            _importResultLog.value = log.toString()
        }
    }

    fun resetSeedData() {
        viewModelScope.launch {
            repository.clearAllData()
            com.example.data.local.AppDatabase.seedInitialData(database)
            showToast("Data awal Pesantrenqu berhasil di-reset!")
        }
    }
}
