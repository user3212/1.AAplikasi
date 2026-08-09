package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JuzInfo
import com.example.data.model.QuranData
import com.example.data.model.Santri
import com.example.ui.viewmodel.PesantrenViewModel

@Composable
fun AturJuzHafalanSantriCard(
    viewModel: PesantrenViewModel,
    modifier: Modifier = Modifier
) {
    val santriList by viewModel.santriList.collectAsState()
    val masterClasses by viewModel.masterClasses.collectAsState()

    var selectedRombel by remember { mutableStateOf("Semua Rombel") }
    var selectedSantri by remember { mutableStateOf<Santri?>(null) }
    var selectedJuzInfo by remember { mutableStateOf<JuzInfo>(QuranData.ALL_JUZ.last()) } // Default Juz 30

    val rombelOptions = remember(santriList, masterClasses) {
        val classesFromSantri = santriList.map { it.kelas }.distinct().filter { it.isNotBlank() }
        val classesFromMaster = masterClasses.map { it.namaKelas }.distinct().filter { it.isNotBlank() }
        listOf("Semua Rombel") + (classesFromSantri + classesFromMaster).distinct().sorted()
    }

    val filteredSantriList = remember(santriList, selectedRombel) {
        if (selectedRombel == "Semua Rombel") {
            santriList
        } else {
            santriList.filter { it.kelas.equals(selectedRombel, ignoreCase = true) }
        }
    }

    // When student is selected, update selectedJuzInfo to match current assigned Juz
    LaunchedEffect(selectedSantri) {
        if (selectedSantri != null) {
            val currentJuzName = viewModel.getSantriJuz(selectedSantri!!.id)
            selectedJuzInfo = QuranData.getJuzInfo(currentJuzName)
        }
    }

    PesantrenCard(accentColor = Color(0xFF10B981), modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFD1FAE5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = Color(0xFF047857),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Atur Juz Hafalan Santri",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Text(
                        text = "Konfigurasi target/juz hafalan untuk tiap santri",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

            // Kolom 1: Dropdown Memilih Rombel Kelas
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Class,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "KOLOM 1: PILIH ROMBEL KELAS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF047857)
                        )
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                CustomDropdown(
                    label = "",
                    options = rombelOptions,
                    selectedOption = selectedRombel,
                    onOptionSelected = {
                        if (it != null) {
                            selectedRombel = it
                            selectedSantri = null
                        }
                    },
                    optionToString = { it },
                    focusAccentColor = Color(0xFF10B981),
                    placeholder = "-- Pilih Rombel Kelas --"
                )
            }

            // Kolom 2: Dropdown Memilih Nama Siswa
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.PersonOutline,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "KOLOM 2: PILIH NAMA SISWA",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF047857)
                        )
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                CustomDropdown(
                    label = "",
                    options = filteredSantriList,
                    selectedOption = selectedSantri,
                    onOptionSelected = { selectedSantri = it },
                    optionToString = { it.nama + if (it.kelas.isNotBlank()) " (${it.kelas})" else "" },
                    focusAccentColor = Color(0xFF10B981),
                    placeholder = "-- Pilih Nama Siswa --"
                )
            }

            // Kolom 3: Dropdown Memilih Data Juz Al-Qur'an (Juz 1 s/d 30 dengan nama & jumlah ayat)
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "KOLOM 3: PILIH DATA JUZ AL-QUR'AN (JUZ 1 - 30)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF047857)
                        )
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                CustomDropdown(
                    label = "",
                    options = QuranData.ALL_JUZ,
                    selectedOption = selectedJuzInfo,
                    onOptionSelected = { if (it != null) selectedJuzInfo = it },
                    optionToString = { it.displayLabel },
                    focusAccentColor = Color(0xFF10B981),
                    placeholder = "-- Pilih Juz Al-Qur'an --"
                )
            }

            // Tombol Simpan
            Button(
                onClick = {
                    if (selectedSantri != null) {
                        viewModel.setSantriJuz(selectedSantri!!.id, selectedJuzInfo.name)
                        viewModel.showToast("Berhasil menyimpan! Santri ${selectedSantri!!.nama} diatur ke ${selectedJuzInfo.displayLabel}")
                    } else {
                        viewModel.showToast("Pilih siswa terlebih dahulu!")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Simpan Pengaturan Hafalan",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}
