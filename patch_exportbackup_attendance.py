import sys

file_path = 'app/src/main/java/com/example/ui/screens/ExportBackupScreen.kt'
with open(file_path, 'r') as f:
    content = f.read()

content = content.replace('attendanceRecords.map { HistorySession(it.tanggal, it.topik) }', 'attendanceRecords.map { HistorySession(it.tanggal, it.sesi) }')

with open(file_path, 'w') as f:
    f.write(content)
print("Patched attendance mapping")
