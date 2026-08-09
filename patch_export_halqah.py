import sys

file_path = 'app/src/main/java/com/example/ui/screens/ExportBackupScreen.kt'
with open(file_path, 'r') as f:
    content = f.read()

replacement = """        val activeHalqah = when {
            selectedMapel.contains("SKI", ignoreCase = true) -> "MAPEL1"
            selectedMapel.contains("Informatika", ignoreCase = true) -> "MAPEL2"
            else -> {
                masterSubjects.find { it.namaMapel == selectedMapel }?.category ?: "UMUM"
            }
        }
        
        val cleanRombel = selectedRombel.replace("Kelas ", "").trim()
        val filteredSantri = santriList.filter { s ->
            val matchRombel = if (selectedRombel == "Semua Rombel") true else s.kelas.replace("Kelas ", "").trim().equals(cleanRombel, ignoreCase = true)
            val matchHalqah = (s.halqah == activeHalqah)
            matchRombel && matchHalqah
        }"""

content = content.replace("""        val cleanRombel = selectedRombel.replace("Kelas ", "").trim()
        val filteredSantri = santriList.filter { s ->
            if (selectedRombel == "Semua Rombel") true
            else s.kelas.replace("Kelas ", "").trim().equals(cleanRombel, ignoreCase = true)
        }""", replacement)

with open(file_path, 'w') as f:
    f.write(content)
print("Patched ExportBackup filter")
