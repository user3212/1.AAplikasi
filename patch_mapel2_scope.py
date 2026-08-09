import sys

file_path = 'app/src/main/java/com/example/ui/screens/Mapel2Screen.kt'

with open(file_path, 'r') as f:
    content = f.read()

# Replace santriList map to filter by halqah
new_content = content.replace('val fromSantri = santriList.map { it.kelas }', 'val fromSantri = santriList.filter { it.halqah == "MAPEL2" }.map { it.kelas }')
new_content = new_content.replace('halqah = "-"', 'halqah = "MAPEL2"')
new_content = new_content.replace('val currentCounts = santriList.groupBy', 'val currentCounts = santriList.filter { it.halqah == "MAPEL2" }.groupBy')
new_content = new_content.replace('val studentsInRombel = santriList.filter { it.kelas == rombelToEdit }', 'val studentsInRombel = santriList.filter { it.kelas == rombelToEdit && it.halqah == "MAPEL2" }')
new_content = new_content.replace('val toDelete = santriList.filter { it.kelas == rombel }', 'val toDelete = santriList.filter { it.kelas == rombel && it.halqah == "MAPEL2" }')
new_content = new_content.replace('val studentsInRombel = remember(santriList, selectedRombel, detailSearch) {\n            santriList.filter { it.kelas == selectedRombel', 'val studentsInRombel = remember(santriList, selectedRombel, detailSearch) {\n            santriList.filter { it.halqah == "MAPEL2" && it.kelas == selectedRombel')
new_content = new_content.replace('val nextNum = santriList.count { it.kelas == selectedRombel } + 1', 'val nextNum = santriList.count { it.kelas == selectedRombel && it.halqah == "MAPEL2" } + 1')

with open(file_path, 'w') as f:
    f.write(new_content)

print("Patched Mapel2")
