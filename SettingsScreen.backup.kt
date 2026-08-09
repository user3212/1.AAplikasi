package com.example.ui.screens

import com.example.ui.components.CustomDropdown

import androidx.compose.material.icons.filled.MenuBook


import androidx.compose.foundation.background

import androidx.compose.foundation.border

import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.height

import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.layout.size

import androidx.compose.foundation.layout.width

import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Add

import androidx.compose.material.icons.filled.Book

import androidx.compose.material.icons.filled.Delete

import androidx.compose.material.icons.filled.Save

import androidx.compose.material.icons.filled.Settings

import androidx.compose.material.icons.filled.Tune

import androidx.compose.material.icons.filled.Visibility

import androidx.compose.material3.Divider

import androidx.compose.material3.Icon

import androidx.compose.material3.IconButton

import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Switch

import androidx.compose.material3.SwitchDefaults

import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.runtime.collectAsState

import androidx.compose.runtime.getValue

import androidx.compose.runtime.mutableStateOf

import androidx.compose.runtime.remember

import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier

import androidx.compose.ui.draw.clip

import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp

import androidx.compose.ui.unit.sp

import com.example.ui.components.CustomInputField

import com.example.ui.components.HeroBanner

import com.example.ui.components.PesantrenCard


import com.example.ui.theme.TahfizAccent




import com.example.ui.viewmodel.PesantrenViewModel

@Composable
fun SettingsScreen(
    viewModel: PesantrenViewModel,
    modifier: Modifier = Modifier
) {
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
    var newCustomSubjectPengampu by remember { mutableStateOf("") }

    var newClassName by remember { mutableStateOf("") }
    var newWaliKelas by remember { mutableStateOf("") }

    var newHalqahName by remember { mutableStateOf("") }
    var newUstaz by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Banner: Pengaturan Modul
        item {
            HeroBanner(
                title = "Pengaturan Modul",
                description = "Kustomisasi sistem agar lebih relevan dengan kurikulum di pondok pesantren Anda. Sembunyikan modul yang tidak diperlukan atau ganti nama mata pelajaran secara instan.",
                icon = Icons.Default.Tune,
                startColor = Color(0xFF6B21A8),
                endColor = Color(0xFF4C1D95),
                borderColor = Color(0xFFA855F7)
            )
        }

        // 1. VISIBILITAS MODUL (SEMBUNYIKAN / TAMPILKAN)
        item {
            PesantrenCard {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.outline),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            text = "Visibilitas Modul\n(Sembunyikan/Tampilkan)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 15.sp,
                                lineHeight = 18.sp
                            )
                        )
                    }

                    Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)

                    // Modul Data Tahfiz
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Modul Data Tahfiz",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            )
                            Text(
                                text = "Sembunyikan jika tidak ada setoran hafalan",
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                        Switch(
                            checked = isTahfizVisible,
                            onCheckedChange = { viewModel.toggleTahfizVisibility(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF10B981)
                            )
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
                            Text(
                                text = "Modul $mapel1Name",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            )
                            Text(
                                text = "Sembunyikan menu Mapel 1",
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                        Switch(
                            checked = isMapel1Visible,
                            onCheckedChange = { viewModel.toggleMapel1Visibility(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF10B981)
                            )
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
                            Text(
                                text = "Modul $mapel2Name",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            )
                            Text(
                                text = "Sembunyikan menu Mapel 2",
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                        Switch(
                            checked = isMapel2Visible,
                            onCheckedChange = { viewModel.toggleMapel2Visibility(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF10B981)
                            )
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
                                Text(
                                    text = "Modul ${customSub.namaMapel}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                )
                                Text(
                                    text = "Sembunyikan menu ${customSub.namaMapel}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                            Switch(
                                checked = customSub.isVisible,
                                onCheckedChange = { viewModel.toggleCustomSubjectVisibility(customSub, it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF10B981)
                                )
                            )
                        }
                    }
                }
            }
        }

        // 2. UBAH NAMA MATA PELAJARAN
        item {
            PesantrenCard {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFEDE9FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Aa",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF6D28D9),
                                    fontSize = 15.sp
                                )
                            )
                        }

                        Text(
                            text = "Ubah Nama Mata Pelajaran",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 15.sp
                            )
                        )
                    }

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
                            .background(Color(0xFF7C3AED))
                            .clickable {
                                viewModel.applySubjectNames(inputMapel1Name, inputMapel2Name)
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Terapkan Nama Baru",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }

        item {
            PesantrenCard {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFD1FAE5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = Color(0xFF047857),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = "Konfigurasi Juz Al-Quran",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 15.sp
                            )
                        )
                    }
                    Text(
                        text = "Pengaturan default Juz untuk Setoran Hafalan.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
                    
                    val juzOptions = (1..30).map { "Juz $it" }
                    var selectedJuz by remember { mutableStateOf(viewModel.getConfigJuz()) }
                    
                    CustomDropdown(
                        label = "PILIH JUZ DEFAULT",
                        options = juzOptions,
                        selectedOption = selectedJuz,
                        onOptionSelected = { 
                            if (it != null) {
                                selectedJuz = it
                                viewModel.setConfigJuz(it)
                                viewModel.showToast("Juz default diubah menjadi $it")
                            }
                        },
                        optionToString = { it },
                        focusAccentColor = Color(0xFF10B981)
                    )
                }
            }
        }
        // 3. TAMBAH MATA PELAJARAN BARU (DUPLIKASI OTOMATIS SEPERTI SKI/INFORMATIKA)
        item {
            PesantrenCard {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFDBEAFE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color(0xFF1D4ED8),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Text(
                            text = "Tambah Mata Pelajaran Baru",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 15.sp
                            )
                        )
                    }

                    Text(
                        text = "Mapel baru akan muncul di sidebar di bawah Informatika dan otomatis terduplikasi dengan fitur Kelola Rombel, Absensi, dan Penilaian (Harian, UTS, PAS).",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)

                    CustomInputField(
                        value = newCustomSubjectName,
                        onValueChange = { newCustomSubjectName = it },
                        label = "NAMA MATA PELAJARAN BARU",
                        placeholder = "Misal: Mapel Fiqih / Bahasa Arab"
                    )

                    CustomInputField(
                        value = newCustomSubjectPengampu,
                        onValueChange = { newCustomSubjectPengampu = it },
                        label = "PENGAMPU (OPSIONAL)",
                        placeholder = "Ustaz Pengampu"
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
                                        pengampu = if (newCustomSubjectPengampu.isNotBlank()) newCustomSubjectPengampu else "Ustaz Pengampu"
                                    )
                                    newCustomSubjectName = ""
                                    newCustomSubjectPengampu = ""
                                }
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Tambah Mapel Ke Sidebar",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    // List of Custom Subjects created
                    val customSubs = masterSubjects.filter { it.category == "CUSTOM" }
                    if (customSubs.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Mata Pelajaran Custom Terdaftar (${customSubs.size}):",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )

                        customSubs.forEach { subj ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF8FAFC))
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Book, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(subj.namaMapel, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground))
                                        Text("Pengampu: ${subj.pengampu}", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                    }
                                }

                                IconButton(onClick = { viewModel.deleteSubject(subj) }) {
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
