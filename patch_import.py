import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # 1. Add state flow for importResultLog
    if 'val importResultLog' not in content:
        import_state = """    private val _importResultLog = MutableStateFlow<String?>(null)
    val importResultLog: StateFlow<String?> = _importResultLog.asStateFlow()

    fun clearImportResultLog() {
        _importResultLog.value = null
    }

"""
        # Insert before fun showLoadingOverlay
        content = content.replace("    fun showLoadingOverlay", import_state + "    fun showLoadingOverlay")

    # 2. Rewrite importBackupJson
    new_import_logic = """
    fun importBackupJson(jsonString: String) {
        viewModelScope.launch {
            val log = StringBuilder()
            log.append("Memulai proses impor database...\\n\\n")
            try {
                val root = org.json.JSONObject(jsonString)
                
                var santriCount = 0
                var tahfizCount = 0
                var pbmGradeCount = 0
                var pbmAttendanceCount = 0

                val existingSantri = repository.getAllSantri().kotlinx.coroutines.flow.first()
                val santriMapByNameAndClass = existingSantri.associateBy { "${it.nama}_${it.kelas}" }.toMutableMap()

                // Function to get or create Santri
                suspend fun getOrCreateSantri(nama: String, kelas: String): Long {
                    val key = "${nama}_${kelas}"
                    val existing = santriMapByNameAndClass[key]
                    if (existing != null) {
                        return existing.id
                    }
                    val newSantri = Santri(nis = "", nama = nama, gender = "L", kelas = kelas, halqah = "", status = "Aktif")
                    val newId = repository.insertSantri(newSantri)
                    santriMapByNameAndClass[key] = newSantri.copy(id = newId)
                    santriCount++
                    return newId
                }

                if (root.has("tahfiz")) {
                    val tahfizArray = root.getJSONArray("tahfiz")
                    for (i in 0 until tahfizArray.length()) {
                        val obj = tahfizArray.getJSONObject(i)
                        val nama = obj.optString("nama", "Fulan")
                        val kelas = obj.optString("kelas", "")
                        
                        val sId = getOrCreateSantri(nama, kelas)
                        
                        val detail = obj.optJSONObject("detail")
                        if (detail != null) {
                            val hafalan = detail.optJSONObject("hafalan")
                            if (hafalan != null) {
                                val keys = hafalan.keys()
                                while (keys.hasNext()) {
                                    val surah = keys.next()
                                    val ayat = hafalan.optInt(surah, 1)
                                    val record = TahfizRecord(
                                        santriId = sId,
                                        santriNama = nama,
                                        tanggal = "2026-08-08", // fallback or could parse if available
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
                                    
                                    val attRecord = AttendanceRecord(
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
                    log.append("- Data Tahfiz berhasil diimpor: $tahfizCount record.\\n")
                }
                
                if (root.has("pbm")) {
                    val pbmArray = root.getJSONArray("pbm")
                    for (i in 0 until pbmArray.length()) {
                        val obj = pbmArray.getJSONObject(i)
                        val nama = obj.optString("nama", "Fulan")
                        val kelas = obj.optString("rombel", "")
                        val mapel = obj.optString("mapel", "Mapel")
                        
                        val sId = getOrCreateSantri(nama, kelas)
                        
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
                                        
                                        val record = GradeRecord(
                                            santriId = sId,
                                            santriNama = nama,
                                            mapelCategory = "MAPEL1",
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
                                    
                                    val attRecord = AttendanceRecord(
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
                    log.append("- Data PBM Nilai berhasil diimpor: $pbmGradeCount record.\\n")
                    log.append("- Data PBM Absensi berhasil diimpor: $pbmAttendanceCount record.\\n")
                }
                
                if (santriCount > 0) {
                    log.append("- Data Santri baru dibuat: $santriCount siswa.\\n")
                }
                
                log.append("\\nStatus: SUCCESS\\nData berhasil disinkronkan ke dalam sistem!")
            } catch (e: Exception) {
                e.printStackTrace()
                log.append("\\nError Parsing File:\\n${e.message}\\n\\nPastikan format JSON sesuai dengan struktur yang didukung aplikasi.")
            }
            
            _importResultLog.value = log.toString()
        }
    }
"""
    # Replace the existing importBackupJson function
    target = re.compile(r'    fun importBackupJson.*?\}\n\s*\}', re.DOTALL)
    content = target.sub(new_import_logic, content)

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('app/src/main/java/com/example/ui/viewmodel/PesantrenViewModel.kt')
