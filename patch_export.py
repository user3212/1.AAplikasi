import sys

file_path = 'app/src/main/java/com/example/ui/screens/ExportBackupScreen.kt'
with open(file_path, 'r') as f:
    content = f.read()

# Add masterSubjects
content = content.replace('val masterClasses by viewModel.masterClasses.collectAsState()', 'val masterClasses by viewModel.masterClasses.collectAsState()\n    val masterSubjects by viewModel.masterSubjects.collectAsState()')

# Change mapelOptions
old_mapel_options = 'val mapelOptions = listOf("Mapel Informatika", "Mapel SKI", "Tahfiz Al-Qur\'an", "Fiqih Ibadah", "Aqidah Akhlak", "Bahasa Arab Dasar")'
new_mapel_options = 'val mapelOptions = remember(masterSubjects) { listOf("Mapel Informatika", "Mapel SKI", "Tahfiz Al-Qur\'an") + masterSubjects.map { it.namaMapel } }.distinct()'
content = content.replace(old_mapel_options, new_mapel_options)

with open(file_path, 'w') as f:
    f.write(content)
print("Patched ExportBackup")
