package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AturJuzHafalanSantriCard
import com.example.ui.components.CustomDropdown
import com.example.ui.components.PesantrenCard
import com.example.ui.viewmodel.PesantrenViewModel
import com.example.data.model.QuranData

@Composable
fun TahfizKonfigurasiScreen(viewModel: PesantrenViewModel, modifier: Modifier = Modifier) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Konfigurasi Tahfiz", "Monitor Data")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = Color(0xFF10B981),
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = Color(0xFF10B981)
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == index) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            when (selectedTabIndex) {
                0 -> KonfigurasiTahfizTab(viewModel)
                1 -> MonitorDataTab(viewModel)
            }
        }
    }
}

@Composable
fun KonfigurasiTahfizTab(viewModel: PesantrenViewModel) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxSize()) {
        item {
            PesantrenCard {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFD1FAE5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color(0xFF047857), modifier = Modifier.size(20.dp))
                        }
                        Text("Konfigurasi Juz Al-Qur'an (Juz 1 - 30)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground))
                    }
                    
                    Text("Pengaturan default Juz Al-Qur'an (Juz 1 s/d 30 lengkap dengan jumlah ayat).", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)

                    var selectedDefaultJuz by remember { mutableStateOf(QuranData.getJuzInfo(viewModel.getConfigJuz())) }

                    CustomDropdown(
                        label = "PILIH JUZ DEFAULT (1 - 30)",
                        options = QuranData.ALL_JUZ,
                        selectedOption = selectedDefaultJuz,
                        onOptionSelected = {
                            if (it != null) {
                                selectedDefaultJuz = it
                                viewModel.setConfigJuz(it.name)
                                viewModel.showToast("Juz default diubah menjadi ${it.displayLabel}")
                            }
                        },
                        optionToString = { it.displayLabel },
                        focusAccentColor = Color(0xFF10B981)
                    )
                }
            }
        }
        
        item {
            AturJuzHafalanSantriCard(viewModel = viewModel)
        }
    }
}

@Composable
fun MonitorDataTab(viewModel: PesantrenViewModel) {
    val santriList by viewModel.santriList.collectAsState()
    // Filter only those who have custom juz assigned
    val customSantriList = santriList.filter { viewModel.hasCustomJuz(it.id) }

    if (customSantriList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Belum ada santri dengan konfigurasi juz khusus.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
            items(customSantriList, key = { it.id }) { santri ->
                val assignedJuzName = viewModel.getSantriJuz(santri.id)
                val juzInfo = QuranData.getJuzInfo(assignedJuzName)
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = santri.nama,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Kelas: ${santri.kelas}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Juz Ditugaskan: ${juzInfo.displayLabel}",
                                color = Color(0xFF047857),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        
                        IconButton(onClick = {
                            viewModel.resetSantriJuz(santri.id)
                            viewModel.showToast("Konfigurasi khusus untuk ${santri.nama} dihapus.")
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus Konfigurasi", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}
