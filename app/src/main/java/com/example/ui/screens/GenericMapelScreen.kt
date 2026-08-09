package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.CustomDropdown
import com.example.ui.components.CustomInputField
import com.example.ui.components.ModuleHeaderBanner
import com.example.ui.components.PesantrenCard
import com.example.ui.viewmodel.CustomSubjectNavState
import com.example.ui.viewmodel.PesantrenViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.io.InputStreamReader
import java.io.BufferedReader
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.People
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.ui.platform.LocalContext

@Composable
fun GenericMapelScreen(
    viewModel: PesantrenViewModel,
    customNav: CustomSubjectNavState,
    modifier: Modifier = Modifier
) {

    val colorPalette = listOf(
        Color(0xFFEA580C), // Orange
        Color(0xFF0D9488), // Teal
        Color(0xFFD97706), // Amber
        Color(0xFF4F46E5), // Indigo
        Color(0xFFDB2777), // Pink
        Color(0xFF65A30D)  // Lime
    )
    val colorIndex = (customNav.subjectId % colorPalette.size).toInt()
    val baseColor = colorPalette[colorIndex]

    when (customNav.subType) {
        "ROMBEL" -> CustomSubjectRombelView(
            viewModel = viewModel,
            subjectName = customNav.subjectName,
            baseColor = baseColor,
            modifier = modifier
        )
        "ABSENSI" -> CustomSubjectAbsensiView(
            viewModel = viewModel,
            subjectName = customNav.subjectName,
            baseColor = baseColor,
            modifier = modifier
        )
        "PENILAIAN_HARIAN" -> CustomSubjectPenilaianView(
            viewModel = viewModel,
            subjectName = customNav.subjectName,
            baseColor = baseColor,
            subjectCategory = "CUSTOM_${customNav.subjectId}",
            title = "Penilaian PBM (${customNav.subjectName})",
            subtitle = "Evaluasi & Nilai Harian",
            jenisUjianDefault = "Penilaian Harian",
            modifier = modifier
        )
        "PENILAIAN_UTS" -> CustomSubjectPenilaianView(
            viewModel = viewModel,
            subjectName = customNav.subjectName,
            baseColor = baseColor,
            subjectCategory = "CUSTOM_${customNav.subjectId}",
            title = "Penilaian UTS (${customNav.subjectName})",
            subtitle = "Evaluasi & Nilai UTS",
            jenisUjianDefault = "Penilaian UTS",
            modifier = modifier
        )
        "PENILAIAN_PAS" -> CustomSubjectPenilaianView(
            viewModel = viewModel,
            subjectName = customNav.subjectName,
            baseColor = baseColor,
            subjectCategory = "CUSTOM_${customNav.subjectId}",
            title = "Penilaian PAS (${customNav.subjectName})",
            subtitle = "Evaluasi & Nilai PAS",
            jenisUjianDefault = "Penilaian PAS",
            modifier = modifier
        )
        else -> CustomSubjectRombelView(
            viewModel = viewModel,
            subjectName = customNav.subjectName,
            baseColor = baseColor,
            modifier = modifier
        )
    }
}

@Composable
fun CustomSubjectRombelView(
    viewModel: PesantrenViewModel,
    subjectName: String,
    baseColor: Color,
    modifier: Modifier = Modifier
) {
    val santriList by viewModel.santriList.collectAsState()
    
    // We use subjectName as halqah because custom mapel category might just be "CUSTOM", 
    // but we need them segregated by mapel. So halqah = "CUSTOM_" + subjectName
    val halqahValue = "CUSTOM_$subjectName"
    
    var currentView by remember { mutableStateOf("LIST_ROMBEL") }
    var selectedRombel by remember { mutableStateOf("") }
    var manualRombels by remember { mutableStateOf(listOf<String>()) }
    
    val allRombels = remember(santriList, manualRombels) {
        val fromSantri = santriList.filter { it.halqah == halqahValue }.map { it.kelas }.filter { it.isNotBlank() }
        (fromSantri + manualRombels).distinct().sorted()
    }
    
    var showEditRombelDialog by remember { mutableStateOf(false) }
    var rombelToEdit by remember { mutableStateOf("") }
    var newRombelName by remember { mutableStateOf("") }

    val context = LocalContext.current
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv"),
        onResult = { uri ->
            if (uri != null) {
                try {
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        val templateData = "Nama Siswa,Rombel\nAhmad Budi,7A\nSiti Aisyah,7A\nDimas,7B\n"
                        outputStream.write(templateData.toByteArray())
                        Unit
                    }
                    viewModel.showToast("Template berhasil disimpan")
                } catch (e: Exception) {
                    viewModel.showToast("Gagal menyimpan template")
                }
                Unit
            }
        }
    )

    val getContentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                try {
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        val reader = BufferedReader(InputStreamReader(inputStream))
                        var imported = 0
                        var isFirstLine = true
                        val currentCounts = santriList.filter { it.halqah == halqahValue }.groupBy { it.kelas }.mapValues { it.value.size }.toMutableMap()
                        reader.forEachLine { line ->
                            if (isFirstLine) {
                                isFirstLine = false
                                return@forEachLine
                            }
                            val parts = line.split(",")
                            if (parts.size >= 2) {
                                val nama = parts[0].trim()
                                val kelasRaw = parts[1].trim()
                                val rombelClean = kelasRaw.replace(Regex("(?i)(kelas|ruang|rombel)"), "").trim().uppercase()
                                if (nama.isNotBlank() && rombelClean.isNotBlank()) {
                                    val count = currentCounts.getOrDefault(rombelClean, 0) + 1
                                    currentCounts[rombelClean] = count
                                    viewModel.saveSantri(nis = "-", nama = nama, gender = "L", kelas = rombelClean, halqah = halqahValue, status = "Aktif", catatan = count.toString())
                                    imported++
                                }
                            }
                        }
                        viewModel.showToast("$imported data siswa berhasil diimpor")
                    }
                } catch (e: Exception) {
                    viewModel.showToast("Gagal membaca file CSV")
                }
                Unit
            }
        }
    )

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
                        val studentsInRombel = santriList.filter { it.kelas == rombelToEdit && it.halqah == halqahValue }
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
                        .background(Brush.horizontalGradient(listOf(baseColor, baseColor.copy(alpha = 0.8f))))
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
                                Text(subjectName, style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(0.7f)))
                            }
                        }
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = { createDocumentLauncher.launch("Template_Siswa.csv") },
                                modifier = Modifier.weight(1f).height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.2f), contentColor = Color.White),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Template", style = MaterialTheme.typography.labelMedium)
                            }
                            Button(
                                onClick = { getContentLauncher.launch("*/*") },
                                modifier = Modifier.weight(1f).height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = baseColor),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Unggah", style = MaterialTheme.typography.labelMedium)
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
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = baseColor)
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
                        colors = ButtonDefaults.buttonColors(containerColor = baseColor)
                    ) {
                        Text("Buat Rombel")
                    }
                }
            }
            
            item {
                if (allRombels.isEmpty()) {
                    Text("Belum ada rombel.", color = Color.Gray, modifier = Modifier.padding(16.dp))
                }
            }
            
            items(allRombels.size) { index ->
                val rombel = allRombels[index]
                val isZebra = index % 2 != 0
                val bgColor = if (isZebra) baseColor.copy(alpha = 0.15f) else baseColor.copy(alpha = 0.05f)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgColor)
                        .clickable {
                            selectedRombel = rombel
                            currentView = "DETAIL_ROMBEL"
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = rombel,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = baseColor),
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
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = baseColor, modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier.size(32.dp).clip(androidx.compose.foundation.shape.CircleShape).background(Color(0xFFFEE2E2)).clickable {
                            val toDelete = santriList.filter { it.kelas == rombel && it.halqah == halqahValue }
                            toDelete.forEach { viewModel.deleteSantri(it) }
                            manualRombels = manualRombels.filter { it != rombel }
                            viewModel.showToast("Rombel dihapus.")
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
        
        val studentsInRombel = remember(santriList, selectedRombel, detailSearch, halqahValue) {
            santriList.filter { it.halqah == halqahValue && it.kelas == selectedRombel && (detailSearch.isEmpty() || it.nama.contains(detailSearch, ignoreCase = true)) }
                .sortedWith(compareBy({ it.catatan.toIntOrNull() ?: 9999 }, { it.nama }))
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
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = baseColor)
                    )
                    Spacer(Modifier.width(12.dp))
                    Button(
                        onClick = {
                            if (addNama.isNotBlank()) {
                                val nextNum = santriList.count { it.kelas == selectedRombel && it.halqah == halqahValue } + 1
                                viewModel.saveSantri(nis = "-", nama = addNama, gender = "L", kelas = selectedRombel, halqah = halqahValue, status = "Aktif", catatan = nextNum.toString())
                                addNama = ""
                            }
                        },
                        modifier = Modifier.height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = baseColor)
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
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = baseColor)
                )
            }
            
            items(studentsInRombel.size) { index ->
                val student = studentsInRombel[index]
                var isEditing by remember { mutableStateOf(false) }
                var editName by remember { mutableStateOf(student.nama) }
                var editNum by remember { mutableStateOf(student.catatan.ifBlank { (index + 1).toString() }) }
                
                val isZebra = index % 2 != 0
                val bgColor = if (isZebra) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface

                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(bgColor).border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isEditing) {
                        OutlinedTextField(
                            value = editNum,
                            onValueChange = { editNum = it.filter { char -> char.isDigit() } },
                            modifier = Modifier.width(60.dp),
                            singleLine = true,
                            label = { Text("No", style = MaterialTheme.typography.labelSmall) },
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.width(8.dp))
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            label = { Text("Nama", style = MaterialTheme.typography.labelSmall) },
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                        IconButton(onClick = {
                            if (editName.isNotBlank()) {
                                viewModel.saveSantri(
                                    id = student.id,
                                    nis = student.nis,
                                    nama = editName,
                                    gender = student.gender,
                                    kelas = student.kelas,
                                    halqah = student.halqah,
                                    status = student.status,
                                    catatan = editNum
                                )
                            }
                            isEditing = false
                        }) {
                            Icon(Icons.Default.Check, contentDescription = "Simpan", tint = Color(0xFF10B981))
                        }
                    } else {
                        val numDisplay = student.catatan.toIntOrNull()?.toString() ?: (index + 1).toString()
                        Text("$numDisplay.", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)), modifier = Modifier.width(32.dp))
                        
                        Text(student.nama, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), modifier = Modifier.weight(1f))
                        IconButton(onClick = { isEditing = true }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = baseColor, modifier = Modifier.size(16.dp))
                        }
                        IconButton(onClick = { viewModel.deleteSantri(student) }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomSubjectAbsensiView(
    viewModel: PesantrenViewModel,
    subjectName: String,
    baseColor: Color,
    modifier: Modifier = Modifier
) {
    val santriList by viewModel.santriList.collectAsState()
    val attendanceRecords by viewModel.attendanceRecords.collectAsState()

    var selectedDate by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
    val statuses = remember { mutableStateMapOf<Long, String>() }

    val activeRecordsForDate = attendanceRecords.filter { it.tanggal == selectedDate && it.sesi == subjectName }

    LaunchedEffect(activeRecordsForDate) {
        activeRecordsForDate.forEach { rec ->
            statuses[rec.santriId] = rec.status
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ModuleHeaderBanner(
                title = "Absensi Kelas ($subjectName)",
                subtitle = "Pencatatan kehadiran harian santri untuk mata pelajaran $subjectName",
                icon = Icons.Default.CheckCircle,
                badgeText = "PRESENSI",
                startColor = baseColor,
                endColor = baseColor.copy(alpha = 0.8f),
                borderColor = baseColor.copy(alpha = 0.5f)
            )
        }

        item {
            PesantrenCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Presensi $selectedDate",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        )

                        CustomInputField(
                            value = selectedDate,
                            onValueChange = { selectedDate = it },
                            label = "TANGGAL",
                            placeholder = "YYYY-MM-DD",
                            modifier = Modifier.width(160.dp)
                        )
                    }

                    Divider(color = MaterialTheme.colorScheme.outline)

                    santriList.forEach { santri ->
                        val currentStatus = statuses[santri.id] ?: "H"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF8FAFC))
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(santri.nama, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground))
                                Text("${santri.kelas} | ${santri.nis}", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf("H", "I", "S", "A").forEach { st ->
                                    val isSelected = currentStatus == st
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                if (isSelected) {
                                                    when (st) {
                                                        "H" -> Color(0xFF10B981)
                                                        "I" -> Color(0xFF3B82F6)
                                                        "S" -> Color(0xFFF59E0B)
                                                        else -> Color(0xFFEF4444)
                                                    }
                                                } else MaterialTheme.colorScheme.outline
                                            )
                                            .clickable {
                                                statuses[santri.id] = st
                                                viewModel.saveAttendance(
                                                    santri = santri,
                                                    tanggal = selectedDate,
                                                    sesi = subjectName,
                                                    status = st,
                                                    keterangan = "Presensi $subjectName"
                                                )
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = st,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomSubjectPenilaianView(
    viewModel: PesantrenViewModel,
    subjectName: String,
    baseColor: Color,
    subjectCategory: String,
    title: String,
    subtitle: String,
    jenisUjianDefault: String,
    modifier: Modifier = Modifier
) {
    val santriList by viewModel.santriList.collectAsState()
    val allGrades by viewModel.gradeRecords.collectAsState()

    var selectedSantri by remember { mutableStateOf<com.example.data.model.Santri?>(null) }
    var nilaiInput by remember { mutableStateOf("") }
    var jenisUjian by remember { mutableStateOf(jenisUjianDefault) }
    var catatanInput by remember { mutableStateOf("") }

    val gradesForThisSubject = allGrades.filter { it.namaMapel == subjectName || it.mapelCategory == subjectCategory }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ModuleHeaderBanner(
                title = title,
                subtitle = subtitle,
                icon = Icons.Default.School,
                badgeText = "EVALUASI",
                startColor = baseColor,
                endColor = baseColor.copy(alpha = 0.8f),
                borderColor = baseColor.copy(alpha = 0.5f)
            )
        }

        item {
            PesantrenCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Input Nilai $subjectName",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    )

                    CustomDropdown(
                        label = "SANTRI",
                        options = listOf("Pilih Santri") + santriList.map { "${it.nama} (${it.kelas})" },
                        selectedOption = selectedSantri?.let { "${it.nama} (${it.kelas})" } ?: "Pilih Santri",
                        onOptionSelected = { str ->
                            selectedSantri = santriList.find { "${it.nama} (${it.kelas})" == str }
                        },
                        optionToString = { it }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CustomInputField(
                            value = nilaiInput,
                            onValueChange = { nilaiInput = it },
                            label = "NILAI (0 - 100)",
                            placeholder = "85",
                            modifier = Modifier.weight(1f)
                        )

                        CustomDropdown(
                            label = "JENIS EVALUASI",
                            options = listOf("Penilaian Harian", "Penilaian UTS", "Penilaian PAS", "Tugas", "Praktek"),
                            selectedOption = jenisUjian,
                            onOptionSelected = { jenisUjian = it },
                            optionToString = { it },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    CustomInputField(
                        value = catatanInput,
                        onValueChange = { catatanInput = it },
                        label = "CATATAN / EVALUASI",
                        placeholder = "Catatan penilaian..."
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(baseColor)
                            .clickable {
                                val s = selectedSantri
                                val n = nilaiInput.toDoubleOrNull()
                                if (s != null && n != null) {
                                    viewModel.addGradeRecord(
                                        santri = s,
                                        mapelCategory = subjectCategory,
                                        namaMapel = subjectName,
                                        tanggal = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                                        jenisUjian = jenisUjian,
                                        nilai = n,
                                        pengampu = "Ustaz Pengampu",
                                        catatan = catatanInput
                                    )
                                    nilaiInput = ""
                                    catatanInput = ""
                                }
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Simpan Nilai $subjectName", style = MaterialTheme.typography.labelMedium.copy(color = Color.White, fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }

        item {
            PesantrenCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Riwayat Penilaian ($subjectName) - Total ${gradesForThisSubject.size}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    )

                    Divider(color = MaterialTheme.colorScheme.outline)

                    if (gradesForThisSubject.isEmpty()) {
                        Text(
                            text = "Belum ada riwayat nilai tercatat.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        gradesForThisSubject.forEach { grade ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF8FAFC))
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(grade.santriNama, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground))
                                    Text("${grade.jenisUjian} | ${grade.tanggal}", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = baseColor.copy(alpha=0.1f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "${grade.nilai}",
                                            style = MaterialTheme.typography.titleMedium.copy(color = baseColor, fontWeight = FontWeight.ExtraBold),
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }

                                    IconButton(onClick = { viewModel.deleteGradeRecord(grade) }) {
                                        Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
