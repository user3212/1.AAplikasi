import re

with open('app/src/main/java/com/example/ui/viewmodel/PesantrenViewModel.kt', 'r') as f:
    content = f.read()

replacement = """                    santriMapByNameAndClass[key] = newSantri.copy(id = newId)
                    
                    val existingClasses = masterClasses.value.map { it.namaKelas }
                    if (!existingClasses.contains(kelas)) {
                        repository.insertClass(MasterClass(namaKelas = kelas, waliKelas = "Wali Kelas"))
                    }
                    
                    santriCount++"""

content = re.sub(
    r'santriMapByNameAndClass\[key\] = newSantri\.copy\(id = newId\)\s*if \(mapelCategory == "UMUM"\) \{\s*val existingClasses = masterClasses\.value\.map \{ it\.namaKelas \}\s*if \(!existingClasses\.contains\(kelas\)\) \{\s*repository\.insertClass\(MasterClass\(namaKelas = kelas, waliKelas = "Wali Kelas"\)\)\s*\}\s*\}\s*santriCount\+\+',
    replacement,
    content
)

with open('app/src/main/java/com/example/ui/viewmodel/PesantrenViewModel.kt', 'w') as f:
    f.write(content)
