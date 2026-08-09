package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import java.io.BufferedReader
import java.io.InputStreamReader
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceRecord
import com.example.data.model.GradeRecord
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.PesantrenViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Mapel2Screen(
    viewModel: PesantrenViewModel,
    navItem: NavItem = NavItem.MAPEL2_ROMBEL,
    modifier: Modifier = Modifier
) {
    when (navItem) {
        NavItem.MAPEL2_ROMBEL -> Mapel2RombelView(viewModel = viewModel, modifier = modifier)
        NavItem.MAPEL2_ABSENSI -> Mapel2AbsensiView(viewModel = viewModel, modifier = modifier)
        NavItem.MAPEL2 -> Mapel2PenilaianView(
            viewModel = viewModel,
            title = "Penilaian PBM",
            subtitle = "Evaluasi & Nilai Harian",
            defaultJenisUjian = "Penilaian Harian",
            modifier = modifier
        )
        NavItem.MAPEL2_UTS -> Mapel2PenilaianView(
            viewModel = viewModel,
            title = "Penilaian UTS",
            subtitle = "Evaluasi & Nilai UTS",
            defaultJenisUjian = "Penilaian UTS",
            modifier = modifier
        )
        NavItem.MAPEL2_PAS -> Mapel2PenilaianView(
            viewModel = viewModel,
            title = "Penilaian PAS",
            subtitle = "Evaluasi & Nilai PAS",
            defaultJenisUjian = "Penilaian PAS",
            modifier = modifier
        )
        else -> UnderConstructionView(
            title = navItem.title,
            subtitle = navItem.subtitle,
            modifier = modifier
        )
    }
}

// =============================================================================
// 1. KELOLA ROMBEL VIEW (MAPEL INFORMATIKA)
// =============================================================================
@Composable
fun Mapel2RombelView(
    viewModel: PesantrenViewModel,
    modifier: Modifier = Modifier
) {
    val santriList by viewModel.santriList.collectAsState()
    
    var currentView by remember { mutableStateOf("LIST_ROMBEL") }
    var selectedRombel by remember { mutableStateOf("") }
    var manualRombels by remember { mutableStateOf(listOf<String>()) }
    
    val allRombels = remember(santriList, manualRombels) {
        val fromSantri = santriList.filter { it.halqah == "MAPEL2" }.map { it.kelas }.filter { it.isNotBlank() }
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
                    }
                    viewModel.showToast("Template berhasil disimpan")
                } catch (e: Exception) {
                    viewModel.showToast("Gagal menyimpan template")
                }
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
                        val currentCounts = santriList.filter { it.halqah == "MAPEL2" }.groupBy { it.kelas }.mapValues { it.value.size }.toMutableMap()
                        reader.forEachLine { line ->
                            if (isFirstLine) {
                                isFirstLine = false
                                return@forEachLine // skip header
                            }
                            val parts = line.split(",")
                            if (parts.size >= 2) {
                                val nama = parts[0].trim()
                                val kelasRaw = parts[1].trim()
                                val rombelClean = kelasRaw.replace(Regex("(?i)(kelas|ruang|rombel)"), "").trim().uppercase()
                                if (nama.isNotBlank() && rombelClean.isNotBlank()) {
                                    val count = currentCounts.getOrDefault(rombelClean, 0) + 1
                                    currentCounts[rombelClean] = count
                                    viewModel.saveSantri(nis = "-", nama = nama, gender = "L", kelas = rombelClean, halqah = "MAPEL2", status = "Aktif", catatan = count.toString())
                                    imported++
                                }
                            }
                        }
                        viewModel.showToast("$imported data siswa berhasil diimpor (Smart Matching)")
                    }
                } catch (e: Exception) {
                    viewModel.showToast("Gagal membaca file. Pastikan format CSV (Comma Separated Values)")
                }
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
                        val studentsInRombel = santriList.filter { it.kelas == rombelToEdit && it.halqah == "MAPEL2" }
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
                            Button(
                                onClick = { createDocumentLauncher.launch("Template_Siswa.csv") },
                                modifier = Modifier.weight(1f).height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.2f), contentColor = Color.White),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Unduh Template", style = MaterialTheme.typography.labelMedium)
                            }
                            Button(
                                onClick = { getContentLauncher.launch("*/*") },
                                modifier = Modifier.weight(1f).height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF6D28D9)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Unggah Excel", style = MaterialTheme.typography.labelMedium)
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
                val isZebra = index % 2 != 0
                val bgColor = if (isZebra) Color(0xFFE9D5FF) else Color(0xFFF3E8FF)
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
                            val toDelete = santriList.filter { it.kelas == rombel && it.halqah == "MAPEL2" }
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
            santriList.filter { it.halqah == "MAPEL2" && it.kelas == selectedRombel && (detailSearch.isEmpty() || it.nama.contains(detailSearch, ignoreCase = true)) }
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
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF7C3AED))
                    )
                    Spacer(Modifier.width(12.dp))
                    Button(
                        onClick = {
                            if (addNama.isNotBlank()) {
                                val nextNum = santriList.count { it.kelas == selectedRombel && it.halqah == "MAPEL2" } + 1
                                viewModel.saveSantri(nis = "-", nama = addNama, gender = "L", kelas = selectedRombel, halqah = "MAPEL2", status = "Aktif", catatan = nextNum.toString())
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
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF7C3AED))
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
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF6B21A8), modifier = Modifier.size(16.dp))
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

// =============================================================================
// 2. ABSENSI KELAS VIEW (MAPEL INFORMATIKA)
// =============================================================================
@Composable
fun Mapel2AbsensiView(
    viewModel: PesantrenViewModel,
    modifier: Modifier = Modifier
) {
    val santriList by viewModel.santriList.collectAsState()
    val attendanceRecords by viewModel.attendanceRecords.collectAsState()

    // Realtime date
    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    var currentDate by remember { mutableStateOf(dateFormatter.format(Date())) }
    var showDatePickerDialog by remember { mutableStateOf(false) }

    // Rombel filter
    val kelasOptions = remember(santriList) {
        listOf("-- Pilih Rombel --") + santriList.map { it.kelas }.distinct().filter { it.isNotBlank() }
    }
    var absensiRombel by remember { mutableStateOf("-- Pilih Rombel --") }
    var absensiSearch by remember { mutableStateOf("") }
    var kelompokBelajarExpanded by remember { mutableStateOf(true) }
    var absensiTab by remember { mutableStateOf(0) } // 0 = Isi Kehadiran, 1 = Rekap Ketidakhadiran

    val filteredAbsensiSantri = remember(santriList, absensiRombel, absensiSearch) {
        if (absensiRombel == "-- Pilih Rombel --") {
            emptyList()
        } else {
            santriList.filter { 
                it.kelas == absensiRombel &&
                (absensiSearch.isEmpty() || it.nama.contains(absensiSearch, ignoreCase = true))
            }.sortedBy { it.nama }
        }
    }

    // Absensi state
    val statusMap = remember(filteredAbsensiSantri, currentDate, attendanceRecords) {
        val map = mutableStateMapOf<Long, String>()
        filteredAbsensiSantri.forEach { map[it.id] = "Hadir" }
        val sesi = "Mapel Informatika"
        attendanceRecords.filter { it.tanggal == currentDate && it.sesi == sesi }.forEach { rec ->
            val status = when (rec.status) {
                "H" -> "Hadir"
                "I" -> "Izin"
                "S" -> "Sakit"
                "A" -> "Alpa"
                "T" -> "Telat"
                else -> rec.status
            }
            if (map.containsKey(rec.santriId)) {
                map[rec.santriId] = status
            }
        }
        map
    }

    val catatanMap = remember(filteredAbsensiSantri, currentDate, attendanceRecords) {
        val map = mutableStateMapOf<Long, String>()
        filteredAbsensiSantri.forEach { map[it.id] = "" }
        val sesi = "Mapel Informatika"
        attendanceRecords.filter { it.tanggal == currentDate && it.sesi == sesi }.forEach { rec ->
            if (map.containsKey(rec.santriId)) {
                map[rec.santriId] = rec.keterangan
            }
        }
        map
    }

    val statusOptions = listOf("Hadir", "Izin", "Sakit", "Alpa", "Telat")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // TOP PURPLE BANNER
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF4C1D95), Color(0xFF6D28D9))
                        )
                    )
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.12f))
                                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CoPresent,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Absensi PBM",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 18.sp
                                    )
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFA78BFA).copy(alpha = 0.3f))
                                        .border(1.dp, Color(0xFFC4B5FD).copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "MAPEL INFORMATIKA",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFFDDD6FE),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Kehadiran Kelas Harian",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.75f),
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    // Date Selector Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                            .clickable { showDatePickerDialog = true }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = formatDbToDisplayDate(currentDate),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // KELOMPOK BELAJAR CARD
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Row(modifier = Modifier.matchParentSize()) {
                    Box(
                        modifier = Modifier
                            .width(5.dp)
                            .fillMaxHeight()
                            .background(Color(0xFF7C3AED))
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { kelompokBelajarExpanded = !kelompokBelajarExpanded },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Groups,
                                contentDescription = null,
                                tint = Color(0xFF7C3AED),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "KELOMPOK BELAJAR",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 13.sp,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            if (absensiRombel != "-- Pilih Rombel --") {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFF3E8FF))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = absensiRombel,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF6B21A8),
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.outline),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (kelompokBelajarExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    if (kelompokBelajarExpanded) {
                        CustomDropdown(
                            label = "",
                            options = kelasOptions,
                            selectedOption = absensiRombel,
                            onOptionSelected = { 
                                absensiRombel = it 
                                kelompokBelajarExpanded = false
                            },
                            optionToString = { it },
                            focusAccentColor = Color(0xFF7C3AED),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = absensiSearch,
                            onValueChange = { absensiSearch = it },
                            placeholder = { Text("Cari nama siswa...", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(18.dp)) },
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(20.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF7C3AED),
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                unfocusedContainerColor = Color(0xFFF8FAFC),
                                focusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                }
            }
        }

        // TABS SWITCHER
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFEEF2F6))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (absensiTab == 0) MaterialTheme.colorScheme.surface else Color.Transparent)
                        .clickable { absensiTab = 0 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Isi Kehadiran",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (absensiTab == 0) FontWeight.Bold else FontWeight.Medium,
                            color = if (absensiTab == 0) Color(0xFF6B21A8) else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (absensiTab == 1) MaterialTheme.colorScheme.surface else Color.Transparent)
                        .clickable { absensiTab = 1 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Rekap Ketidakhadiran",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (absensiTab == 1) FontWeight.Bold else FontWeight.Medium,
                            color = if (absensiTab == 1) Color(0xFF6B21A8) else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    )
                }
            }
        }

        // TABLE CONTAINER (HEADER + BODY)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            ) {
                // Dark Navy Header Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF223248))
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "NO",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp,
                                letterSpacing = 0.5.sp
                            ),
                            modifier = Modifier.width(32.dp)
                        )
                        Text(
                            text = "NAMA SISWA",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp,
                                letterSpacing = 0.5.sp
                            ),
                            modifier = Modifier.weight(1.2f)
                        )
                        Text(
                            text = "STATUS",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp,
                                letterSpacing = 0.5.sp
                            ),
                            modifier = Modifier.weight(0.7f)
                        )
                        Text(
                            text = "CATATAN",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp,
                                letterSpacing = 0.5.sp
                            ),
                            modifier = Modifier.weight(0.9f)
                        )
                    }
                }

                // Table Body Content
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(vertical = 8.dp)
                ) {
                    if (absensiRombel == "-- Pilih Rombel --") {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(androidx.compose.foundation.shape.CircleShape)
                                    .background(Color(0xFFF3E8FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FilterAlt,
                                    contentDescription = null,
                                    tint = Color(0xFFA78BFA),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "PILIH ROMBEL DAHULU",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp,
                                    letterSpacing = 0.8.sp
                                )
                            )
                        }
                    } else {
                        if (absensiTab == 0) {
                            // Tab 0: Isi Kehadiran
                            if (filteredAbsensiSantri.isEmpty()) {
                                Text(
                                    text = "Tidak ada siswa ditemukan di rombel $absensiRombel",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)),
                                    modifier = Modifier.padding(24.dp)
                                )
                            } else {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    filteredAbsensiSantri.forEachIndexed { index, santri ->
                                        val currentStatus = statusMap[santri.id] ?: "Hadir"
                                        val currentCatatan = catatanMap[santri.id] ?: ""

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 10.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.weight(1.2f),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(
                                                    text = "${index + 1}.",
                                                    style = MaterialTheme.typography.labelMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    ),
                                                    modifier = Modifier.width(32.dp)
                                                )
                                                Text(
                                                    text = santri.nama,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onBackground,
                                                        fontSize = 13.sp
                                                    ),
                                                    maxLines = 2
                                                )
                                            }

                                            Box(modifier = Modifier.weight(0.7f)) {
                                                CustomDropdown(
                                                    label = "",
                                                    options = statusOptions,
                                                    selectedOption = currentStatus,
                                                    onOptionSelected = { statusMap[santri.id] = it },
                                                    optionToString = { it },
                                                    focusAccentColor = Color(0xFF7C3AED),
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            }

                                            Box(modifier = Modifier.weight(0.9f)) {
                                                CustomInputField(
                                                    value = currentCatatan,
                                                    onValueChange = { catatanMap[santri.id] = it },
                                                    label = "",
                                                    placeholder = "Keterangan...",
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            }
                                        }
                                        Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
                                    }
                                }
                            }
                        } else {
                            // Tab 1: Rekap Ketidakhadiran
                            val nonHadirList = filteredAbsensiSantri.filter {
                                val st = statusMap[it.id] ?: "Hadir"
                                st != "Hadir"
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    val totalS = filteredAbsensiSantri.count { (statusMap[it.id] ?: "Hadir") == "Sakit" }
                                    val totalI = filteredAbsensiSantri.count { (statusMap[it.id] ?: "Hadir") == "Izin" }
                                    val totalA = filteredAbsensiSantri.count { (statusMap[it.id] ?: "Hadir") == "Alpa" }
                                    val totalT = filteredAbsensiSantri.count { (statusMap[it.id] ?: "Hadir") == "Telat" }
                                    val totalH = filteredAbsensiSantri.count { (statusMap[it.id] ?: "Hadir") == "Hadir" }

                                    Text("Hadir: $totalH", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF059669)))
                                    Text("Sakit: $totalS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFFD97706)))
                                    Text("Izin: $totalI", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF2563EB)))
                                    Text("Alpa: $totalA", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFFDC2626)))
                                    Text("Telat: $totalT", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF7C3AED)))
                                }

                                if (nonHadirList.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 20.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Semua siswa di Rombel $absensiRombel HADIR pada tanggal ${formatDbToDisplayDate(currentDate)}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF059669), fontWeight = FontWeight.Bold)
                                        )
                                    }
                                } else {
                                    nonHadirList.forEach { santri ->
                                        val st = statusMap[santri.id] ?: "Hadir"
                                        val cat = catatanMap[santri.id] ?: ""
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFF8FAFC))
                                                .padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text(santri.nama, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                                if (cat.isNotBlank()) {
                                                    Text("Ket: $cat", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)))
                                                }
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(
                                                        when (st) {
                                                            "Sakit" -> Color(0xFFFEF3C7)
                                                            "Izin" -> Color(0xFFDBEAFE)
                                                            "Alpa" -> Color(0xFFFEE2E2)
                                                            "Telat" -> Color(0xFFF3E8FF)
                                                            else -> Color(0xFFD1FAE5)
                                                        }
                                                    )
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = st,
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = when (st) {
                                                            "Sakit" -> Color(0xFFD97706)
                                                            "Izin" -> Color(0xFF1D4ED8)
                                                            "Alpa" -> Color(0xFFDC2626)
                                                            "Telat" -> Color(0xFF7C3AED)
                                                            else -> Color(0xFF059669)
                                                        }
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

        // BOTTOM SAVE BUTTON
        if (absensiRombel != "-- Pilih Rombel --") {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF7C3AED))
                        .clickable {
                            val listToSave = filteredAbsensiSantri.map { s ->
                                val statusStr = statusMap[s.id] ?: "Hadir"
                                val code = when (statusStr) {
                                    "Hadir" -> "H"
                                    "Izin" -> "I"
                                    "Sakit" -> "S"
                                    "Alpa" -> "A"
                                    "Telat" -> "T"
                                    else -> "H"
                                }
                                val cat = catatanMap[s.id] ?: ""
                                AttendanceRecord(
                                    santriId = s.id,
                                    santriNama = s.nama,
                                    tanggal = currentDate,
                                    sesi = "Mapel Informatika",
                                    status = code,
                                    keterangan = cat
                                )
                            }
                            viewModel.saveBatchAttendance(listToSave)
                        }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Simpan Kehadiran Kelas",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        )
                    }
                }
            }
        }
    }

    if (showDatePickerDialog) {
        com.example.ui.components.ModernDatePickerDialog(
            initialDateMillis = null,
            onDateSelected = { millis ->
                currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(java.util.Date(millis))
            },
            onDismiss = { showDatePickerDialog = false }
        )
    }
}

// =============================================================================
// 3. PENILAIAN VIEW (MAPEL INFORMATIKA - HARIAN, UTS, PAS)
// =============================================================================
@Composable
fun Mapel2PenilaianView(
    viewModel: PesantrenViewModel,
    title: String = "Penilaian PBM",
    subtitle: String = "Evaluasi & Nilai Harian",
    defaultJenisUjian: String = "Penilaian Harian",
    modifier: Modifier = Modifier
) {
    val santriList by viewModel.santriList.collectAsState()
    val gradeRecords by viewModel.gradeRecords.collectAsState()

    // Realtime date: defaults to today's date automatically in real-time when opened
    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    var currentDate by remember { mutableStateOf(dateFormatter.format(Date())) }
    var showDatePickerDialog by remember { mutableStateOf(false) }

    // Rombel & Settings
    val kelasOptions = remember(santriList) {
        listOf("-- Pilih Rombel --") + santriList.map { it.kelas }.distinct().filter { it.isNotBlank() }
    }
    var selectedRombel by remember { mutableStateOf("-- Pilih Rombel --") }
    var materiTopic by remember { mutableStateOf("") }
    var massScoreInput by remember { mutableStateOf("0") }
    var searchKeyword by remember { mutableStateOf("") }
    var settingsExpanded by remember { mutableStateOf(true) }

    // Filtered Santri List
    val filteredSantri = remember(santriList, selectedRombel, searchKeyword) {
        if (selectedRombel == "-- Pilih Rombel --") {
            emptyList()
        } else {
            santriList.filter {
                it.kelas == selectedRombel &&
                (searchKeyword.isEmpty() || it.nama.contains(searchKeyword, ignoreCase = true))
            }.sortedBy { it.nama }
        }
    }

    // Score & Catatan Maps
    val scoreMap = remember(filteredSantri, currentDate, gradeRecords, materiTopic, defaultJenisUjian) {
        val map = mutableStateMapOf<Long, String>()
        filteredSantri.forEach { map[it.id] = "0" }
        gradeRecords.filter {
            it.mapelCategory == "MAPEL2" &&
            it.namaMapel == "Informatika" &&
            it.tanggal == currentDate &&
            it.jenisUjian == (if (materiTopic.isBlank()) defaultJenisUjian else materiTopic)
        }.forEach { rec ->
            if (map.containsKey(rec.santriId)) {
                map[rec.santriId] = if (rec.nilai % 1.0 == 0.0) rec.nilai.toInt().toString() else rec.nilai.toString()
            }
        }
        map
    }

    val noteMap = remember(filteredSantri, currentDate, gradeRecords, materiTopic, defaultJenisUjian) {
        val map = mutableStateMapOf<Long, String>()
        filteredSantri.forEach { map[it.id] = "" }
        gradeRecords.filter {
            it.mapelCategory == "MAPEL2" &&
            it.namaMapel == "Informatika" &&
            it.tanggal == currentDate &&
            it.jenisUjian == (if (materiTopic.isBlank()) defaultJenisUjian else materiTopic)
        }.forEach { rec ->
            if (map.containsKey(rec.santriId)) {
                map[rec.santriId] = rec.catatan
            }
        }
        map
    }

    // Date picker dialog state
    
    if (showDatePickerDialog) {
        com.example.ui.components.ModernDatePickerDialog(
            initialDateMillis = null,
            onDateSelected = { millis ->
                currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(java.util.Date(millis))
            },
            onDismiss = { showDatePickerDialog = false }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // TOP BANNER CARD: PENILAIAN PBM (MAPEL INFORMATIKA)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF5B21B6), Color(0xFF7C3AED))
                        )
                    )
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Badge Icon A+
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.12f))
                                .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "A+",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontSize = 18.sp
                                )
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 18.sp
                                    )
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFA78BFA).copy(alpha = 0.3f))
                                        .border(1.dp, Color(0xFFC4B5FD).copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "MAPEL INFORMATIKA",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFFDDD6FE),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.75f),
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    // Date Selector Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                            .clickable {
                                                                showDatePickerDialog = true
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = formatDbToDisplayDate(currentDate),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // PENGATURAN & PENCARIAN CARD
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Row(modifier = Modifier.matchParentSize()) {
                    Box(
                        modifier = Modifier
                            .width(5.dp)
                            .fillMaxHeight()
                            .background(Color(0xFF7C3AED))
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header Row with Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { settingsExpanded = !settingsExpanded },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                tint = Color(0xFF7C3AED),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "PENGATURAN & PENCARIAN",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 13.sp,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            if (selectedRombel != "-- Pilih Rombel --") {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFF3E8FF))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = selectedRombel,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF6B21A8),
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.outline),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (settingsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    if (settingsExpanded) {
                        // 1. PILIH ROMBEL
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Groups, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                                Text(
                                    text = "PILIH ROMBEL",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                            }
                            CustomDropdown(
                                label = "",
                                options = kelasOptions,
                                selectedOption = selectedRombel,
                                onOptionSelected = { selectedRombel = it },
                                optionToString = { it },
                                focusAccentColor = Color(0xFF7C3AED),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // 2. MATERI / TOPIK
                        val isPasOrUts = defaultJenisUjian.contains("PAS", ignoreCase = true) || defaultJenisUjian.contains("UTS", ignoreCase = true)
                        if (!isPasOrUts) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                                    Text(
                                        text = "MATERI / TOPIK",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.sp,
                                            letterSpacing = 0.5.sp
                                        )
                                    )
                                }
                                CustomInputField(
                                    value = materiTopic,
                                    onValueChange = { materiTopic = it },
                                    label = "",
                                    placeholder = "Materi (Cth: Pemrograman Dasar)",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        // 3. INPUT NILAI (Mass Score fill)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Percent, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                                Text(
                                    text = "INPUT NILAI",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, Color(0xFF7C3AED).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF3E8FF).copy(alpha = 0.5f)),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = massScoreInput,
                                    onValueChange = { massScoreInput = it },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.Transparent,
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent
                                    ),
                                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                )

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(Color(0xFF8B5CF6), Color(0xFF7C3AED))
                                            )
                                        )
                                        .clickable {
                                            if (filteredSantri.isNotEmpty()) {
                                                filteredSantri.forEach { s ->
                                                    scoreMap[s.id] = massScoreInput
                                                }
                                                viewModel.showToast("Nilai $massScoreInput diterapkan ke seluruh siswa")
                                            } else {
                                                viewModel.showToast("Pilih Rombel terlebih dahulu")
                                            }
                                        }
                                        .padding(horizontal = 20.dp, vertical = 14.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "SIMPAN",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                letterSpacing = 0.5.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // 4. SEARCH BAR
                        OutlinedTextField(
                            value = searchKeyword,
                            onValueChange = { searchKeyword = it },
                            placeholder = { Text("Cari nama siswa...", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(18.dp)) },
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(20.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF7C3AED),
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                unfocusedContainerColor = Color(0xFFF8FAFC),
                                focusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                }
            }
        }

        // TABLE CONTAINER (HEADER + BODY)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            ) {
                // Dark Navy Header Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF223248))
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "NO",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp,
                                letterSpacing = 0.5.sp
                            ),
                            modifier = Modifier.width(32.dp)
                        )
                        Text(
                            text = "NAMA SISWA",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp,
                                letterSpacing = 0.5.sp
                            ),
                            modifier = Modifier.weight(1.2f)
                        )
                        Text(
                            text = "NILAI",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp,
                                letterSpacing = 0.5.sp
                            ),
                            modifier = Modifier.weight(0.6f)
                        )
                        Text(
                            text = "CATATAN",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp,
                                letterSpacing = 0.5.sp
                            ),
                            modifier = Modifier.weight(1.0f)
                        )
                    }
                }

                // Table Body
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(vertical = 8.dp)
                ) {
                    if (selectedRombel == "-- Pilih Rombel --") {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(androidx.compose.foundation.shape.CircleShape)
                                    .background(Color(0xFFF3E8FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FilterAlt,
                                    contentDescription = null,
                                    tint = Color(0xFFA78BFA),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "PILIH ROMBEL DAHULU",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp,
                                    letterSpacing = 0.8.sp
                                )
                            )
                        }
                    } else if (filteredSantri.isEmpty()) {
                        Text(
                            text = "Tidak ada siswa ditemukan di Rombel $selectedRombel",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.padding(24.dp)
                        )
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            filteredSantri.forEachIndexed { index, santri ->
                                val currentScore = scoreMap[santri.id] ?: "0"
                                val currentNote = noteMap[santri.id] ?: ""

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // 1. NAMA SISWA
                                    Row(
                                        modifier = Modifier.weight(1.2f),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "${index + 1}.",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            ),
                                            modifier = Modifier.width(32.dp)
                                        )
                                        Text(
                                            text = santri.nama,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onBackground,
                                                fontSize = 13.sp
                                            ),
                                            maxLines = 2
                                        )
                                    }

                                    // 2. NILAI (Score Box)
                                    Box(modifier = Modifier.weight(0.6f)) {
                                        OutlinedTextField(
                                            value = currentScore,
                                            onValueChange = { scoreMap[santri.id] = it },
                                            modifier = Modifier.fillMaxWidth().height(46.dp),
                                            singleLine = true,
                                            shape = RoundedCornerShape(10.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Color(0xFF7C3AED),
                                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                                unfocusedContainerColor = Color(0xFFF8FAFC),
                                                focusedContainerColor = MaterialTheme.colorScheme.surface
                                            ),
                                            textStyle = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onBackground,
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                                fontSize = 13.sp
                                            )
                                        )
                                    }

                                    // 3. CATATAN
                                    Box(modifier = Modifier.weight(1.0f)) {
                                        CustomInputField(
                                            value = currentNote,
                                            onValueChange = { noteMap[santri.id] = it },
                                            label = "",
                                            placeholder = "Catatan opsional...",
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                                Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // BOTTOM SAVE BUTTON
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color(0xFF6D28D9), Color(0xFF5B21B6))
                                        )
                                    )
                                    .clickable {
                                        val recordsToSave = filteredSantri.map { s ->
                                            val sc = (scoreMap[s.id] ?: "0").toDoubleOrNull() ?: 0.0
                                            val nt = noteMap[s.id] ?: ""
                                            GradeRecord(
                                                santriId = s.id,
                                                santriNama = s.nama,
                                                mapelCategory = "MAPEL2",
                                                namaMapel = "Informatika",
                                                tanggal = currentDate,
                                                jenisUjian = materiTopic.ifBlank { defaultJenisUjian },
                                                nilai = sc,
                                                pengampu = "Ustaz Informatika",
                                                catatan = nt
                                            )
                                        }
                                        viewModel.saveBatchGrades(recordsToSave)
                                    }
                                    .padding(vertical = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Simpan Seluruh Nilai",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 15.sp
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
