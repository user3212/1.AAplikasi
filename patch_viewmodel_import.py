import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    import_logic = """
    fun importBackupJson(jsonString: String) {
        viewModelScope.launch {
            try {
                val root = JSONObject(jsonString)
                
                // Clear existing data before import? Or just add?
                // Let's assume we want to import and replace or add.
                // We'll clear the relevant tables if the import has them.
                
                var importedCount = 0
                
                if (root.has("tahfiz")) {
                    repository.clearAllData() // simple way, though maybe just clear tahfiz
                    
                    val tahfizArray = root.getJSONArray("tahfiz")
                    for (i in 0 until tahfizArray.length()) {
                        val obj = tahfizArray.getJSONObject(i)
                        val record = TahfizRecord(
                            santriId = obj.optLong("santriId", 0L),
                            santriNama = obj.optString("santriNama", ""),
                            tanggal = obj.optString("tanggal", ""),
                            jenisSetoran = obj.optString("jenisSetoran", "Ziyadah"),
                            surahJuz = obj.optString("surahJuz", ""),
                            ayatMulai = obj.optString("ayatMulai", ""),
                            ayatSelesai = obj.optString("ayatSelesai", ""),
                            nilai = obj.optString("nilai", "Mumtaz (A)"),
                            pengampu = obj.optString("pengampu", "Ustaz Halqah"),
                            catatan = obj.optString("catatan", "")
                        )
                        repository.insertTahfizRecord(record)
                        importedCount++
                    }
                }
                
                if (root.has("pbm")) {
                    val pbmArray = root.getJSONArray("pbm")
                    for (i in 0 until pbmArray.length()) {
                        val obj = pbmArray.getJSONObject(i)
                        // A pbm record could be grade or attendance
                        // Try to guess based on keys
                        if (obj.has("jenisUjian") || obj.has("nilai")) {
                            val record = GradeRecord(
                                santriId = obj.optLong("santriId", 0L),
                                santriNama = obj.optString("santriNama", ""),
                                mapelCategory = obj.optString("mapelCategory", "MAPEL1"),
                                namaMapel = obj.optString("namaMapel", "Mapel"),
                                tanggal = obj.optString("tanggal", ""),
                                jenisUjian = obj.optString("jenisUjian", "Tugas"),
                                nilai = obj.optDouble("nilai", 0.0),
                                pengampu = obj.optString("pengampu", "Ustaz"),
                                catatan = obj.optString("catatan", "")
                            )
                            repository.insertGradeRecord(record)
                            importedCount++
                        } else if (obj.has("sesi") || obj.has("status")) {
                            val record = AttendanceRecord(
                                santriId = obj.optLong("santriId", 0L),
                                santriNama = obj.optString("santriNama", ""),
                                tanggal = obj.optString("tanggal", ""),
                                sesi = obj.optString("sesi", "KBM Pagi"),
                                status = obj.optString("status", "H"),
                                keterangan = obj.optString("keterangan", "")
                            )
                            repository.insertAttendanceRecord(record)
                            importedCount++
                        }
                    }
                }

                // If standard structure exists
                if (root.has("grades")) {
                    val gradesArray = root.getJSONArray("grades")
                    for (i in 0 until gradesArray.length()) {
                        val obj = gradesArray.getJSONObject(i)
                        val record = GradeRecord(
                            santriId = obj.optLong("santriId", 0L),
                            santriNama = obj.optString("santriNama", ""),
                            mapelCategory = obj.optString("mapelCategory", "MAPEL1"),
                            namaMapel = obj.optString("namaMapel", "Mapel"),
                            tanggal = obj.optString("tanggal", ""),
                            jenisUjian = obj.optString("jenisUjian", "Tugas"),
                            nilai = obj.optDouble("nilai", 0.0),
                            pengampu = obj.optString("pengampu", "Ustaz"),
                            catatan = obj.optString("catatan", "")
                        )
                        repository.insertGradeRecord(record)
                        importedCount++
                    }
                }
                
                if (root.has("attendance")) {
                    val attendanceArray = root.getJSONArray("attendance")
                    for (i in 0 until attendanceArray.length()) {
                        val obj = attendanceArray.getJSONObject(i)
                        val record = AttendanceRecord(
                            santriId = obj.optLong("santriId", 0L),
                            santriNama = obj.optString("santriNama", ""),
                            tanggal = obj.optString("tanggal", ""),
                            sesi = obj.optString("sesi", "KBM Pagi"),
                            status = obj.optString("status", "H"),
                            keterangan = obj.optString("keterangan", "")
                        )
                        repository.insertAttendanceRecord(record)
                        importedCount++
                    }
                }

                if (root.has("santri")) {
                    val santriArray = root.getJSONArray("santri")
                    for (i in 0 until santriArray.length()) {
                        val obj = santriArray.getJSONObject(i)
                        val record = Santri(
                            nis = obj.optString("nis", ""),
                            nama = obj.optString("nama", ""),
                            gender = obj.optString("gender", "L"),
                            kelas = obj.optString("kelas", ""),
                            halqah = obj.optString("halqah", ""),
                            status = obj.optString("status", "Aktif"),
                            catatan = obj.optString("catatan", "")
                        )
                        repository.insertSantri(record)
                        importedCount++
                    }
                }

                showToast("Berhasil mengimpor data")
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("Gagal membaca atau memproses file backup")
            }
        }
    }
"""
    # Insert before resetSeedData
    content = content.replace("fun resetSeedData() {", import_logic + "\n    fun resetSeedData() {")

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('app/src/main/java/com/example/ui/viewmodel/PesantrenViewModel.kt')
