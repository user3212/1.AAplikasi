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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CustomInputField
import com.example.ui.components.ExcelSmartParserDialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.PesantrenViewModel
import java.io.InputStreamReader
import java.io.BufferedReader
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.CheckCircle

@Composable
fun SantriScreen(
    viewModel: PesantrenViewModel,
    modifier: Modifier = Modifier
) {
    val santriList by viewModel.santriList.collectAsState()
    
    var currentView by remember { mutableStateOf("LIST_ROMBEL") }
    var selectedRombel by remember { mutableStateOf("") }
    var manualRombels by remember { mutableStateOf(listOf<String>()) }
    
    val allRombels = remember(santriList, manualRombels) {
        val fromSantri = santriList.filter { it.halqah == "UMUM" }.map { it.kelas }.filter { it.isNotBlank() }
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
                        val currentCounts = santriList.filter { it.halqah == "UMUM" }.groupBy { it.kelas }.mapValues { it.value.size }.toMutableMap()
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
                                    viewModel.saveSantri(nis = "-", nama = nama, gender = "L", kelas = rombelClean, halqah = "UMUM", status = "Aktif", catatan = count.toString())
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
                        val studentsInRombel = santriList.filter { it.kelas == rombelToEdit && it.halqah == "UMUM" }
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
                        .background(Brush.horizontalGradient(listOf(Color(0xFF064E3B), Color(0xFF047857))))
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
                                Text("Manajemen Data Santri", style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(0.7f)))
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
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF10B981)),
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
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF10B981))
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
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
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
                val bgColor = if (isZebra) Color(0xFFD1FAE5) else Color(0xFFECFDF5)
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
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF047857)),
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
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF047857), modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier.size(32.dp).clip(androidx.compose.foundation.shape.CircleShape).background(Color(0xFFFEE2E2)).clickable {
                            val toDelete = santriList.filter { it.kelas == rombel && it.halqah == "UMUM" }
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
        
        val studentsInRombel = remember(santriList, selectedRombel, detailSearch) {
            santriList.filter { it.halqah == "UMUM" && it.kelas == selectedRombel && (detailSearch.isEmpty() || it.nama.contains(detailSearch, ignoreCase = true)) }
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
                    Text("Data Santri: $selectedRombel", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
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
                        placeholder = { Text("Nama Santri Baru...") },
                        modifier = Modifier.weight(1f).height(50.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF10B981))
                    )
                    Spacer(Modifier.width(12.dp))
                    Button(
                        onClick = {
                            if (addNama.isNotBlank()) {
                                val nextNum = santriList.count { it.kelas == selectedRombel && it.halqah == "UMUM" } + 1
                                viewModel.saveSantri(nis = "-", nama = addNama, gender = "L", kelas = selectedRombel, halqah = "UMUM", status = "Aktif", catatan = nextNum.toString())
                                addNama = ""
                            }
                        },
                        modifier = Modifier.height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Text("Tambah")
                    }
                }
            }
            
            item {
                OutlinedTextField(
                    value = detailSearch,
                    onValueChange = { detailSearch = it },
                    placeholder = { Text("Cari santri...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF10B981))
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
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF047857), modifier = Modifier.size(16.dp))
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
