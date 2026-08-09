package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuranData
import com.example.ui.components.CustomDropdown
import com.example.ui.components.CustomInputField
import com.example.ui.components.ExcelSmartParserDialog
import com.example.ui.components.HeroBanner
import com.example.ui.components.PesantrenCard
import com.example.ui.viewmodel.PesantrenViewModel

@Composable
fun SettingsScreen(
    viewModel: PesantrenViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val masterClasses by viewModel.masterClasses.collectAsState()
    val masterHalqahs by viewModel.masterHalqahs.collectAsState()
    val masterSubjects by viewModel.masterSubjects.collectAsState()
    val mapel1Name by viewModel.mapel1Name.collectAsState()
    val mapel2Name by viewModel.mapel2Name.collectAsState()
    val isTahfizVisible by viewModel.isTahfizVisible.collectAsState()
    val isMapel1Visible by viewModel.isMapel1Visible.collectAsState()
    val isMapel2Visible by viewModel.isMapel2Visible.collectAsState()

    var inputMapel1Name by remember(mapel1Name) { mutableStateOf(mapel1Name) }
    var inputMapel2Name by remember(mapel2Name) { mutableStateOf(mapel2Name) }

    var newCustomSubjectName by remember { mutableStateOf("") }

    var isKonfigurasiModulExpanded by remember { mutableStateOf(false) }
    var isTambahMapelExpanded by remember { mutableStateOf(true) }
    var isUbahNamaMapelExpanded by remember { mutableStateOf(false) }

    var showExcelDialog by remember { mutableStateOf(false) }

    // Launcher Unggah Excel / CSV Data
    val pickSpreadsheetLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val contentStr = inputStream.bufferedReader().use { it.readText() }
                    val lines = contentStr.split("\n")
                    val pairs = mutableListOf<Pair<String, String>>()
                    for (line in lines.drop(1)) {
                        val parts = line.split(",")
                        if (parts.size >= 2) {
                            val nama = parts[0].trim().removeSurrounding("\"")
                            val rombel = parts[1].trim().removeSurrounding("\"")
                            if (nama.isNotEmpty() && rombel.isNotEmpty()) {
                                pairs.add(nama to rombel)
                            }
                        }
                    }
                    if (pairs.isNotEmpty()) {
                        viewModel.importSantriBatch(pairs)
                        viewModel.showToast("Berhasil mengimpor ${pairs.size} data santri & rombel")
                    } else {
                        viewModel.showToast("Data kosong atau format CSV/Excel tidak sesuai")
                    }
                }
            } catch (e: Exception) {
                viewModel.showToast("Gagal membaca file: ${e.message}")
            }
        }
    }

    // Launcher Unduh Template
    val downloadTemplateLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write("Nama,Rombel\nAhmad Yazid,7A\nAl Faiz Zoka,7A\nAnanda Ryla Kenzi,7B\nArifqy,7B".toByteArray())
                }
                viewModel.showToast("Template Excel/CSV berhasil diunduh")
            } catch (e: Exception) {
                viewModel.showToast("Gagal mengunduh template")
            }
        }
    }

    if (showExcelDialog) {
        ExcelSmartParserDialog(
            title = "Unggah Excel / CSV",
            subtitle = "Sistem Smart Parser Duplikasi Manajemen Santri",
            samplePresets = listOf(
                "Sample Data Rombel & Mapel" to "Ahmad Yazid, 7A\nAl Faiz Zoka, 7A\nAnanda Ryla Kenzi, 7B\nArifqy, 7B\nFadlan Al Fatirh, 7C"
            ),
            onDismiss = { showExcelDialog = false },
            onImport = { pairs ->
                viewModel.importSantriBatch(pairs)
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HeroBanner(
                title = "Fitur Tambahan",
                description = "Kustomisasi sistem agar lebih relevan dengan kurikulum di pondok pesantren Anda. Sesuaikan fitur sesuai kebutuhan instansi.",
                icon = Icons.Default.Tune,
                startColor = Color(0xFF6B21A8),
                endColor = Color(0xFF4C1D95),
                borderColor = Color(0xFFA855F7)
            )
        }

        item {
            PesantrenCard {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isKonfigurasiModulExpanded = !isKonfigurasiModulExpanded },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFEDE9FE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Visibility, contentDescription = null, tint = Color(0xFF6D28D9), modifier = Modifier.size(18.dp))
                            }
                            Text("Konfigurasi Modul", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground))
                        }
                        Icon(if (isKonfigurasiModulExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null)
                    }
                    
                    AnimatedVisibility(visible = isKonfigurasiModulExpanded) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
                            
                            // Modul Data Tahfiz
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Modul Data Tahfiz", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground))
                                    Text("Sembunyikan jika tidak ada setoran hafalan", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                }
                                Switch(
                                    checked = isTahfizVisible,
                                    onCheckedChange = { viewModel.toggleTahfizVisibility(it) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF10B981))
                                )
                            }
                            Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
                            
                            // Modul Mapel SKI / Mapel 1
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Modul \$mapel1Name", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground))
                                    Text("Sembunyikan menu Mapel 1", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                }
                                Switch(
                                    checked = isMapel1Visible,
                                    onCheckedChange = { viewModel.toggleMapel1Visibility(it) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF10B981))
                                )
                            }
                            Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
                            
                            // Modul Mapel Informatika / Mapel 2
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Modul \$mapel2Name", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground))
                                    Text("Sembunyikan menu Mapel 2", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                }
                                Switch(
                                    checked = isMapel2Visible,
                                    onCheckedChange = { viewModel.toggleMapel2Visibility(it) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF10B981))
                                )
                            }
                            
                            // Custom Subjects Visibilities
                            val customSubs = masterSubjects.filter { it.category == "CUSTOM" }
                            customSubs.forEach { customSub ->
                                Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Modul \${customSub.namaMapel}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground))
                                        Text("Sembunyikan menu \${customSub.namaMapel}", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                    }
                                    Switch(
                                        checked = customSub.isVisible,
                                        onCheckedChange = { viewModel.toggleCustomSubjectVisibility(customSub, it) },
                                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF10B981))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            PesantrenCard {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isUbahNamaMapelExpanded = !isUbahNamaMapelExpanded },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFE0F2FE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Aa", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = Color(0xFF0284C7), fontSize = 15.sp))
                            }
                            Text("Ubah Nama Mata Pelajaran", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground))
                        }
                        Icon(if (isUbahNamaMapelExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null)
                    }

                    AnimatedVisibility(visible = isUbahNamaMapelExpanded) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
                            CustomInputField(
                                value = inputMapel1Name,
                                onValueChange = { inputMapel1Name = it },
                                label = "NAMA MAPEL 1 (DEFAULT: MAPEL SKI)",
                                placeholder = "Mapel SKI"
                            )
                            CustomInputField(
                                value = inputMapel2Name,
                                onValueChange = { inputMapel2Name = it },
                                label = "NAMA MAPEL 2 (DEFAULT: MAPEL INFORMATIKA)",
                                placeholder = "Mapel Informatika"
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF0284C7))
                                    .clickable { viewModel.applySubjectNames(inputMapel1Name, inputMapel2Name) }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Terapkan Nama Baru", style = MaterialTheme.typography.labelLarge.copy(color = Color.White, fontWeight = FontWeight.Bold))
                                }
                            }
                        }
                    }
                }
            }
        }


        item {
            PesantrenCard {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isTambahMapelExpanded = !isTambahMapelExpanded },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFDBEAFE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF1D4ED8), modifier = Modifier.size(20.dp))
                            }
                            Text("Tambah Mapel & Unggah Excel Data", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground))
                        }
                        Icon(if (isTambahMapelExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null)
                    }

                    AnimatedVisibility(visible = isTambahMapelExpanded) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Text("Mapel baru akan muncul di sidebar di bawah Informatika dan otomatis terduplikasi dengan fitur Kelola Rombel, Absensi, dan Penilaian (Harian, UTS, PAS). Dilengkapi juga dengan fitur Unggah Excel.", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                            Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)

                            // Fitur Unggah Excel & Download Template Duplikasi dari Manajemen Santri
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { downloadTemplateLauncher.launch("Template_Data.csv") },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF475569)),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Template Excel", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = { showExcelDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Unggah Excel", fontSize = 12.sp)
                                }
                            }

                            Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)

                            CustomInputField(
                                value = newCustomSubjectName,
                                onValueChange = { newCustomSubjectName = it },
                                label = "NAMA MATA PELAJARAN BARU",
                                placeholder = "Misal: Mapel Fiqih / Bahasa Arab"
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF2563EB))
                                    .clickable {
                                        if (newCustomSubjectName.isNotBlank()) {
                                            viewModel.addCustomSubjectModule(
                                                namaMapel = newCustomSubjectName,
                                                pengampu = "Ustaz Pengampu"
                                            )
                                            newCustomSubjectName = ""
                                        }
                                    }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Tambah Mapel Ke Sidebar", style = MaterialTheme.typography.labelLarge.copy(color = Color.White, fontWeight = FontWeight.Bold))
                                }
                            }

                            val customSubs = masterSubjects.filter { it.category == "CUSTOM" }
                            if (customSubs.isNotEmpty()) {
                                Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
                                Text("DAFTAR MAPEL CUSTOM", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant))
                                customSubs.forEach { sub ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surface)
                                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier.size(8.dp).clip(androidx.compose.foundation.shape.CircleShape).background(Color(0xFF10B981))
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(sub.namaMapel, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                            }
                                        }
                                        IconButton(onClick = { viewModel.deleteSubject(sub) }, modifier = Modifier.size(24.dp)) {
                                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red)
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
}
