import sys

file_path = 'app/src/main/java/com/example/ui/screens/ExportBackupScreen.kt'
with open(file_path, 'r') as f:
    content = f.read()

old_logic = """        val activeHalqah = when {
            selectedMapel.contains("SKI", ignoreCase = true) -> "MAPEL1"
            selectedMapel.contains("Informatika", ignoreCase = true) -> "MAPEL2"
            else -> {
                masterSubjects.find { it.namaMapel == selectedMapel }?.category ?: "UMUM"
            }
        }"""

new_logic = """        val activeHalqah = when {
            selectedMapel.contains("SKI", ignoreCase = true) -> "MAPEL1"
            selectedMapel.contains("Informatika", ignoreCase = true) -> "MAPEL2"
            else -> {
                val subject = masterSubjects.find { it.namaMapel == selectedMapel }
                if (subject?.category == "CUSTOM") {
                    "CUSTOM_${selectedMapel}"
                } else {
                    subject?.category ?: "UMUM"
                }
            }
        }"""

content = content.replace(old_logic, new_logic)

with open(file_path, 'w') as f:
    f.write(content)
print("Patched ExportBackup filter 2")
