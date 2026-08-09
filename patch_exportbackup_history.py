import sys

file_path = 'app/src/main/java/com/example/ui/screens/ExportBackupScreen.kt'
with open(file_path, 'r') as f:
    content = f.read()

bad_history = """    val historySessions = remember(selectedMapel) {
        if (selectedMapel.contains("SKI")) {
            listOf(
                HistorySession("2026-08-05", "Sejarah Peradaban Islam Bab 1"),
                HistorySession("2026-07-28", "Kebudayaan Masyarakat Jahiliyah")
            )

        }
    }"""

good_history = """    val historySessions = remember(selectedMapel, gradeRecords, attendanceRecords) {
        val gradeDates = gradeRecords.filter { it.namaMapel.contains(selectedMapel, ignoreCase = true) || selectedMapel.contains(it.namaMapel, ignoreCase = true) }.map { HistorySession(it.tanggal, it.jenisUjian) }
        val attendanceDates = attendanceRecords.map { HistorySession(it.tanggal, it.topik) }
        val allDates = (gradeDates + attendanceDates).distinctBy { it.date }.sortedByDescending { it.date }
        if (allDates.isNotEmpty()) allDates else listOf(HistorySession("2026-08-05", "PBM"))
    }"""

content = content.replace(bad_history, good_history)

with open(file_path, 'w') as f:
    f.write(content)
print("Patched historySessions")
