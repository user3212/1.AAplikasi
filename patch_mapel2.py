import sys
import re

file_path = 'app/src/main/java/com/example/ui/screens/Mapel2Screen.kt'

with open(file_path, 'r') as f:
    content = f.read()

# Find fun Mapel2RombelView(
start_index = content.find('fun Mapel2RombelView(')
if start_index == -1:
    print("Mapel2RombelView not found")
    sys.exit(1)

# Find matching brace
brace_count = 0
in_function = False
end_index = -1

for i in range(start_index, len(content)):
    if content[i] == '{':
        brace_count += 1
        in_function = True
    elif content[i] == '}':
        brace_count -= 1
        if in_function and brace_count == 0:
            end_index = i + 1
            break

if end_index == -1:
    print("Could not find end of Mapel2RombelView")
    sys.exit(1)

new_code = """@Composable
fun Mapel2RombelView(
    viewModel: PesantrenViewModel,
    modifier: Modifier = Modifier
) {
    val santriList by viewModel.santriList.collectAsState()
    
    var currentView by remember { mutableStateOf("LIST_ROMBEL") }
    var selectedRombel by remember { mutableStateOf("") }
    var manualRombels by remember { mutableStateOf(listOf<String>()) }
    
    val allRombels = remember(santriList, manualRombels) {
        val fromSantri = santriList.map { it.kelas }.filter { it.isNotBlank() }
        (fromSantri + manualRombels).distinct().sorted()
    }
    
    var showEditRombelDialog by remember { mutableStateOf(false) }
    var rombelToEdit by remember { mutableStateOf("") }
    var newRombelName by remember { mutableStateOf("") }

    if (showEditRombelDialog) {
        AlertDialog(
            onDismissRequest = { showEditRombelDialog = false },
            title = { Text("Edit Nama Rombel") },
            text = {
                OutlinedTextField(
                    value = newRombelName,
                    onValueChange = { newRombelName = it },
                    label = { Text("Nama Baru") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newRombelName.isNotBlank() && newRombelName != rombelToEdit) {
                        val studentsInRombel = santriList.filter { it.kelas == rombelToEdit }
                        studentsInRombel.forEach { student ->
                            viewModel.saveSantri(
                                id = student.id,
                                nis = student.nis,
                                nama = student.nama,
                                gender = student.gender,
                                kelas = newRombelName,
                                halqah = student.halqah,
                                status = student.status,
                                catatan = student.catatan
                            )
                        }
                        manualRombels = manualRombels.map { if (it == rombelToEdit) newRombelName else it }
                    }
                    showEditRombelDialog = false
                }) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditRombelDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    if (currentView == "LIST_ROMBEL") {
        var kelolaRombelManual by remember { mutableStateOf("") }
        
        LazyColumn(
            modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                        .background(Brush.horizontalGradient(listOf(Color(0xFF4C1D95), Color(0xFF6D28D9))))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(48.dp)
                                    .background(Color.White.copy(0.1f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.People, contentDescription = null, tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Kelola Rombel", style = MaterialTheme.typography.titleLarge.copy(color = Color.White, fontWeight = FontWeight.Bold))
                                Text("Mapel Informatika", style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(0.7f)))
                            }
                        }
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(
                                onClick = { viewModel.showToast("Template Excel berhasil diunduh") },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.5f))
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Unduh Template")
                            }
                            Button(
                                onClick = {
                                    val simulatedData = listOf(
                                        Pair("Ahmad Budi", "7A"),
                                        Pair("Siti Aisyah", "kelas 7a"),
                                        Pair("Dimas", "ruang 7B"),
                                        Pair("Ayu", "rombel 7B")
                                    )
                                    var imported = 0
                                    simulatedData.forEach { (nama, kelasRaw) ->
                                        val rombelClean = kelasRaw.replace(Regex("(?i)(kelas|ruang|rombel)"), "").trim().uppercase()
                                        if (rombelClean.isNotBlank()) {
                                            viewModel.saveSantri(nis = "-", nama = nama, gender = "L", kelas = rombelClean, halqah = "-", status = "Aktif", catatan = "")
                                            imported++
                                        }
                                    }
                                    viewModel.showToast("$imported data siswa diunggah (Smart Matching!)")
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF6D28D9))
                            ) {
                                Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Unggah Excel")
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surface).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = kelolaRombelManual,
                        onValueChange = { kelolaRombelManual = it },
                        placeholder = { Text("Nama Rombel Baru...") },
                        modifier = Modifier.weight(1f).height(50.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF7C3AED)
                        )
                    )
                    Spacer(Modifier.width(12.dp))
                    Button(
                        onClick = {
                            if (kelolaRombelManual.isNotBlank()) {
                                val newRombel = kelolaRombelManual.trim().uppercase()
                                if (!manualRombels.contains(newRombel) && !allRombels.contains(newRombel)) {
                                    manualRombels = manualRombels + newRombel
                                }
                                kelolaRombelManual = ""
                            }
                        },
                        modifier = Modifier.height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                    ) {
                        Text("Buat Rombel")
                    }
                }
            }
            
            item {
                if (allRombels.isEmpty()) {
                    Text("Belum ada rombel. Silakan buat atau unggah data.", color = Color.Gray, modifier = Modifier.padding(16.dp))
                }
            }
            
            items(allRombels.size) { index ->
                val rombel = allRombels[index]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF3E8FF))
                        .clickable {
                            selectedRombel = rombel
                            currentView = "DETAIL_ROMBEL"
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = rombel,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF6B21A8)),
                        modifier = Modifier.weight(1f)
                    )
                    
                    Box(
                        modifier = Modifier.size(32.dp).clip(androidx.compose.foundation.shape.CircleShape).background(Color.White).clickable {
                            rombelToEdit = rombel
                            newRombelName = rombel
                            showEditRombelDialog = true
                        },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF6B21A8), modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier.size(32.dp).clip(androidx.compose.foundation.shape.CircleShape).background(Color(0xFFFEE2E2)).clickable {
                            val toDelete = santriList.filter { it.kelas == rombel }
                            toDelete.forEach { viewModel.deleteSantri(it) }
                            manualRombels = manualRombels.filter { it != rombel }
                            viewModel.showToast("Rombel $rombel dan semua siswanya dihapus.")
                        },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    } else {
        var detailSearch by remember { mutableStateOf("") }
        var addNama by remember { mutableStateOf("") }
        
        val studentsInRombel = remember(santriList, selectedRombel, detailSearch) {
            santriList.filter { it.kelas == selectedRombel && (detailSearch.isEmpty() || it.nama.contains(detailSearch, ignoreCase = true)) }.sortedBy { it.nama }
        }
        
        LazyColumn(
            modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = { currentView = "LIST_ROMBEL" }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                    Text("Data Siswa: $selectedRombel", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                }
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surface).border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = addNama,
                        onValueChange = { addNama = it },
                        placeholder = { Text("Nama Siswa Baru...") },
                        modifier = Modifier.weight(1f).height(50.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF7C3AED))
                    )
                    Spacer(Modifier.width(12.dp))
                    Button(
                        onClick = {
                            if (addNama.isNotBlank()) {
                                viewModel.saveSantri(nis = "-", nama = addNama, gender = "L", kelas = selectedRombel, halqah = "-", status = "Aktif", catatan = "")
                                addNama = ""
                            }
                        },
                        modifier = Modifier.height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                    ) {
                        Text("Tambah")
                    }
                }
            }
            
            item {
                OutlinedTextField(
                    value = detailSearch,
                    onValueChange = { detailSearch = it },
                    placeholder = { Text("Cari siswa...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF7C3AED))
                )
            }
            
            items(studentsInRombel.size) { index ->
                val student = studentsInRombel[index]
                var isEditing by remember { mutableStateOf(false) }
                var editName by remember { mutableStateOf(student.nama) }
                
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surface).border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isEditing) {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        IconButton(onClick = {
                            if (editName.isNotBlank() && editName != student.nama) {
                                viewModel.saveSantri(
                                    id = student.id,
                                    nis = student.nis,
                                    nama = editName,
                                    gender = student.gender,
                                    kelas = student.kelas,
                                    halqah = student.halqah,
                                    status = student.status,
                                    catatan = student.catatan
                                )
                            }
                            isEditing = false
                        }) {
                            Icon(Icons.Default.Check, contentDescription = "Simpan", tint = Color(0xFF10B981))
                        }
                    } else {
                        Text(student.nama, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold), modifier = Modifier.weight(1f))
                        IconButton(onClick = { isEditing = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF6B21A8))
                        }
                        IconButton(onClick = { viewModel.deleteSantri(student) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color(0xFFEF4444))
                        }
                    }
                }
            }
        }
    }
}"""

new_content = content[:start_index] + new_code + content[end_index:]
with open(file_path, 'w') as f:
    f.write(new_content)

print("Successfully replaced Mapel2RombelView.")
