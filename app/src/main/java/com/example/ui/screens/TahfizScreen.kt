package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import java.io.OutputStreamWriter
import androidx.compose.ui.platform.LocalContext

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Santri
import com.example.data.model.QuranData
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.PesantrenViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.Context
import android.graphics.pdf.PdfDocument
import android.graphics.Paint
import android.graphics.Bitmap
import android.graphics.Canvas
import com.example.data.model.TahfizRecord


@Composable
fun TahfizScreen(viewModel: PesantrenViewModel, navItem: NavItem, modifier: Modifier = Modifier) {
    when (navItem) {
        NavItem.TAHFIZ -> SetoranHafalanView(viewModel = viewModel, modifier = modifier)
        NavItem.EVALUASI_TAHFIZ -> EvaluasiKualitasView(viewModel = viewModel, modifier = modifier)
        NavItem.REKAP_TAHFIZ -> RekapitulasiHafalanView(viewModel = viewModel, modifier = modifier)
        else -> SetoranHafalanView(viewModel = viewModel, modifier = modifier)
    }
}

@Composable
fun SetoranHafalanView(viewModel: PesantrenViewModel, modifier: Modifier = Modifier) {
    val santriList by viewModel.santriList.collectAsState()
    val tahfizRecords by viewModel.tahfizRecords.collectAsState()
    
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    var tanggal by remember { mutableStateOf(dateFormatter.format(Date())) }
    val context = LocalContext.current
    var showDatePickerDialog by remember { mutableStateOf(false) }
    
    if (showDatePickerDialog) {
        com.example.ui.components.ModernDatePickerDialog(
            initialDateMillis = null,
            onDateSelected = { millis ->
                tanggal = dateFormatter.format(java.util.Date(millis))
            },
            onDismiss = { showDatePickerDialog = false }
        )
    }
    
    var selectedSantri by remember { mutableStateOf<Santri?>(null) }
    var santriJuz by remember(selectedSantri) {
        mutableStateOf(if (selectedSantri != null) viewModel.getSantriJuz(selectedSantri!!.id) else viewModel.getConfigJuz())
    }
    
    val juzInfo = remember(santriJuz) {
        QuranData.getJuzInfo(santriJuz)
    }

    val surahJuzData = remember(juzInfo) {
        juzInfo.surahs
    }

    var selectedSurahPair by remember { mutableStateOf<Pair<String, Int>?>(null) }
    var tambahanAyat by remember { mutableStateOf("0") }
    
    val capaianAyatTerakhir = remember(selectedSantri, selectedSurahPair, tahfizRecords) {
        if (selectedSantri == null || selectedSurahPair == null) 0 else {
            val records = tahfizRecords.filter { it.santriId == selectedSantri!!.id && it.surahJuz == selectedSurahPair!!.first }
            if (records.isNotEmpty()) records.maxOf { it.ayatSelesai.toIntOrNull() ?: 0 } else 0
        }
    }

    LaunchedEffect(selectedSantri, tahfizRecords, santriJuz) {
        if (selectedSantri != null) {
            val records = tahfizRecords.filter { it.santriId == selectedSantri!!.id }
            if (records.isNotEmpty()) {
                val lastRecord = records.maxByOrNull { it.id }
                if (lastRecord != null) {
                    val lastSurah = surahJuzData.find { it.first == lastRecord.surahJuz }
                    if (lastSurah != null) {
                        if (lastRecord.ayatSelesai.toIntOrNull() == lastSurah.second) {
                            val idx = surahJuzData.indexOf(lastSurah)
                            if (idx != -1 && idx < surahJuzData.size - 1) {
                                selectedSurahPair = surahJuzData[idx + 1]
                            } else {
                                selectedSurahPair = surahJuzData.last()
                            }
                        } else {
                            selectedSurahPair = lastSurah
                        }
                    } else {
                         selectedSurahPair = surahJuzData.firstOrNull()
                    }
                }
            } else {
                selectedSurahPair = surahJuzData.firstOrNull()
            }
        }
    }

    var showJuzCompletedPopup by remember { mutableStateOf(false) }

    if (showJuzCompletedPopup) {
        var nextSelectedJuz by remember { mutableStateOf(QuranData.ALL_JUZ.firstOrNull { it.name != santriJuz } ?: QuranData.ALL_JUZ.first()) }
        AlertDialog(
            onDismissRequest = { showJuzCompletedPopup = false },
            title = { Text("Juz Selesai!", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Alhamdulillah, santri telah menyelesaikan ${juzInfo.displayLabel}. Silakan pilih juz selanjutnya untuk santri ini:", style = MaterialTheme.typography.bodyMedium)
                    CustomDropdown(
                        label = "PILIH JUZ SELANJUTNYA",
                        options = QuranData.ALL_JUZ,
                        selectedOption = nextSelectedJuz,
                        onOptionSelected = { if (it != null) nextSelectedJuz = it },
                        optionToString = { it.displayLabel },
                        focusAccentColor = Color(0xFF10B981)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { 
                    if (selectedSantri != null) {
                        viewModel.setSantriJuz(selectedSantri!!.id, nextSelectedJuz.name)
                        santriJuz = nextSelectedJuz.name
                    }
                    showJuzCompletedPopup = false
                }) {
                    Text("Simpan Juz", color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showJuzCompletedPopup = false }) {
                    Text("Tutup", color = Color.Gray)
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Box(modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF115E59))
                .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF047857))
                                .border(1.dp, Color(0xFF10B981).copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Setoran Hafalan", style = MaterialTheme.typography.titleLarge.copy(color = Color.White, fontWeight = FontWeight.Bold))
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(modifier = Modifier.clip(RoundedCornerShape(16.dp)).border(1.dp, Color(0xFF10B981), RoundedCornerShape(16.dp)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                                    Text("TAHFIZ", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text("Input hafalan ayat harian", style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.9f)))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF34D399).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                        .clickable { showDatePickerDialog = true }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(tanggal, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }
        }
        
        
        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PersonOutline, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("PILIH SANTRI", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF4B5563)))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        CustomDropdown(
                            label = "",
                            options = santriList,
                            selectedOption = selectedSantri,
                            onOptionSelected = { 
                                selectedSantri = it
                                if (it != null) {
                                    santriJuz = viewModel.getSantriJuz(it.id)
                                }
                            },
                            optionToString = { it.nama + if (it.kelas.isNotBlank()) " (${it.kelas})" else "" },
                            focusAccentColor = Color(0xFF10B981),
                            placeholder = "-- Pilih Nama Santri --"
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("SURAH (${juzInfo.displayLabel.uppercase()})", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF4B5563)))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        CustomDropdown(
                            label = "",
                            options = surahJuzData,
                            selectedOption = selectedSurahPair,
                            onOptionSelected = { selectedSurahPair = it; tambahanAyat = "0" },
                            optionToString = { it.first + " (${it.second} Ayat)" },
                            focusAccentColor = Color(0xFF10B981),
                            placeholder = "-- Pilih Surah --"
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier.weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF8FAFC))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("CAPAIAN AYAT TERAKHIR", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF64748B), fontSize = 10.sp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(if (selectedSantri == null || selectedSurahPair == null) "-" else capaianAyatTerakhir.toString(), style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF334155)))
                            }
                        }
                        
                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("TAMBAHAN AYAT BARU", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF10B981), fontSize = 10.sp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = tambahanAyat,
                                    onValueChange = { tambahanAyat = it },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color(0xFFECFDF5),
                                        unfocusedContainerColor = Color(0xFFECFDF5),
                                        focusedBorderColor = Color(0xFF6EE7B7),
                                        unfocusedBorderColor = Color(0xFFA7F3D0)
                                    ),
                                    textStyle = androidx.compose.ui.text.TextStyle(textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontSize = 20.sp, color = Color(0xFF4B5563)),
                                    singleLine = true,
                                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .height(56.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF6EE7B7))
                                        .clickable {
                                            if (selectedSurahPair != null) {
                                                val sisa = selectedSurahPair!!.second - capaianAyatTerakhir
                                                tambahanAyat = sisa.toString()
                                            }
                                        }
                                        .padding(horizontal = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                        Icon(Icons.Default.CheckCircleOutline, contentDescription = null, tint = Color(0xFF064E3B), modifier = Modifier.size(20.dp))
                                        Text("TUNTAS", color = Color(0xFF064E3B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (selectedSantri != null && selectedSurahPair != null) {
                                val tambah = tambahanAyat.toIntOrNull() ?: 0
                                val sisa = selectedSurahPair!!.second - capaianAyatTerakhir
                                if (tambah <= 0) {
                                    viewModel.showToast("Masukkan tambahan ayat yang valid.")
                                } else if (tambah > sisa) {
                                    viewModel.showToast("Ayat berlebih! Sisa ayat di surah ini adalah $sisa")
                                    tambahanAyat = sisa.toString()
                                } else {
                                    val newTotal = capaianAyatTerakhir + tambah
                                    val maxAyat = selectedSurahPair!!.second
                                    viewModel.addTahfizRecord(
                                        santri = selectedSantri!!,
                                        tanggal = tanggal,
                                        jenisSetoran = "Ziyadah",
                                        surahJuz = selectedSurahPair!!.first,
                                        ayatMulai = (capaianAyatTerakhir + 1).toString(),
                                        ayatSelesai = newTotal.toString(),
                                        nilai = "Mumtaz (A)",
                                        pengampu = "Ustaz",
                                        catatan = if (newTotal >= maxAyat) "Tuntas" else "Belum Tuntas"
                                    )
                                    viewModel.showToast("Setoran berhasil disimpan")
                                    tambahanAyat = "0"
                                    
                                    if (newTotal >= maxAyat) {
                                        val idx = surahJuzData.indexOf(selectedSurahPair)
                                        if (idx != -1 && idx < surahJuzData.size - 1) {
                                            selectedSurahPair = surahJuzData[idx + 1]
                                        } else {
                                            showJuzCompletedPopup = true
                                        }
                                    }
                                }
                            } else {
                                viewModel.showToast("Pilih santri dan surah.")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircleOutline, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Simpan Tambahan Setoran", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }

        item {
            if (selectedSantri != null) {
                val lastRecord = tahfizRecords.filter { it.santriId == selectedSantri!!.id }.maxByOrNull { it.id }
                if (lastRecord != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E293B))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(16.dp), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.WifiTethering, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("HISTORY SANTRI", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold, letterSpacing = 1.sp))
                                }
                                Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(Color(0xFF334155)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                    Text(lastRecord.tanggal, color = Color(0xFF6EE7B7), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF293548))
                                    .padding(16.dp)
                            ) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text(selectedSantri!!.nama, color = Color(0xFF6EE7B7), fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(lastRecord.surahJuz, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(lastRecord.ayatSelesai, color = Color(0xFF6EE7B7), fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(0.5f), textAlign = TextAlign.End)
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
fun EvaluasiKualitasView(viewModel: PesantrenViewModel, modifier: Modifier = Modifier) {
    val santriList by viewModel.santriList.collectAsState()
    val tahfizRecords by viewModel.tahfizRecords.collectAsState()

    var selectedRombel by remember { mutableStateOf<String?>(null) }
    
    val rombelOptions = remember(santriList) {
        listOf(null) + santriList.map { it.kelas }.distinct().filter { it.isNotBlank() }
    }
    
    val filteredSantriList = remember(santriList, selectedRombel) {
        if (selectedRombel == null) santriList else santriList.filter { it.kelas == selectedRombel }
    }
    
    var selectedSantri by remember { mutableStateOf<Santri?>(null) }
    var selectedSurah by remember { mutableStateOf<String?>(null) }
    var selectedKualitas by remember { mutableStateOf("A (Sangat Baik)") }

    val surahList = listOf("An-Naba'", "An-Nazi'at", "'Abasa", "At-Takwir", "Al-Infitar", "Al-Mutaffifin")

    LazyColumn(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ModuleHeaderBanner(
                title = "Evaluasi Kualitas",
                subtitle = "Penilaian kelancaran & tajwid",
                icon = Icons.Default.Star,
                badgeText = "EVALUASI",
                startColor = Color(0xFF064E3B),
                endColor = Color(0xFF047857),
                rightActions = {}
            )
        }
        item {
            PesantrenCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CustomDropdown(
                        label = "SANTRI YANG DIEVALUASI",
                        options = filteredSantriList,
                        selectedOption = selectedSantri,
                        onOptionSelected = { selectedSantri = it },
                        optionToString = { it.nama },
                        focusAccentColor = Color(0xFF10B981)
                    )
                    CustomDropdown(
                        label = "SURAH YANG DINILAI",
                        options = surahList,
                        selectedOption = selectedSurah,
                        onOptionSelected = { selectedSurah = it },
                        optionToString = { it },
                        focusAccentColor = Color(0xFF10B981)
                    )
                    Text("NILAI KUALITAS HAFALAN", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    val options = listOf("A (Sangat Baik)", "B (Baik)", "C (Cukup)", "D (Kurang)")
                    options.forEach { option ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = selectedKualitas == option, onClick = { selectedKualitas = option })
                            Text(option)
                        }
                    }
                    Button(
                        onClick = { viewModel.showToast("Evaluasi disimpan!") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Simpan Evaluasi")
                    }
                }
            }
        }
    }
}
@Composable
fun RekapitulasiHafalanView(
    viewModel: PesantrenViewModel,
    modifier: Modifier = Modifier
) {
    val santriList by viewModel.santriList.collectAsState()
    val tahfizRecords by viewModel.tahfizRecords.collectAsState()
    var filterSort by remember { mutableStateOf("Semua Santri (Diurutkan dari Tertinggi)") }
    val sortOptions = listOf(
        "Semua Santri (Diurutkan dari Tertinggi)",
        "Semua Santri (Diurutkan dari Terendah)",
        "Filter Per Kelas"
    )
    
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    var startDate by remember { mutableStateOf(dateFormatter.format(Date(System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000))) }
    var endDate by remember { mutableStateOf(dateFormatter.format(Date())) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var selectedRombel by remember { mutableStateOf<String?>(null) }
    
    val rombelOptions = remember(santriList) {
        listOf(null) + santriList.map { it.kelas }.distinct().filter { it.isNotBlank() }
    }
    
    fun isDateInRange(dateStr: String, startStr: String, endStr: String): Boolean {
        return try {
            val date = dateFormatter.parse(dateStr)
            val start = dateFormatter.parse(startStr)
            val end = dateFormatter.parse(endStr)
            if (date != null && start != null && end != null) {
                !date.before(start) && !date.after(end)
            } else {
                true
            }
        } catch (e: Exception) {
            true
        }
    }

    val exportPdfLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri: Uri? ->
        if (uri != null) {
            try {
                val pdfDocument = PdfDocument()
                val paint = Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 12f
                    isAntiAlias = true
                }
                
                val filteredSantri = if (selectedRombel != null) santriList.filter { it.kelas == selectedRombel } else santriList
                val santriGroupedByRombel = filteredSantri.groupBy { it.kelas }.toSortedMap()
                
                for ((rombel, santris) in santriGroupedByRombel) {
                    var pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                    var page = pdfDocument.startPage(pageInfo)
                    var canvas = page.canvas
                    var y = 50f
                    
                    paint.isFakeBoldText = true
                    paint.textSize = 16f
                    canvas.drawText("Laporan Rekapitulasi Hafalan Tahfiz - Rombel: $rombel", 50f, y, paint)
                    y += 20f
                    paint.textSize = 12f
                    canvas.drawText("Periode: $startDate - $endDate", 50f, y, paint)
                    y += 40f
                    
                    canvas.drawText("Nama Santri", 50f, y, paint)
                    canvas.drawText("Rombel", 250f, y, paint)
                    canvas.drawText("Jumlah Juz", 330f, y, paint)
                    canvas.drawText("Jumlah Surah", 410f, y, paint)
                    canvas.drawText("Penilaian", 500f, y, paint)
                    y += 20f
                    
                    paint.isFakeBoldText = false
                    
                    for (santri in santris) {
                        if (y > 800f) {
                            pdfDocument.finishPage(page)
                            pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                            page = pdfDocument.startPage(pageInfo)
                            canvas = page.canvas
                            y = 50f
                            
                            paint.isFakeBoldText = true
                            canvas.drawText("Nama Santri", 50f, y, paint)
                            canvas.drawText("Rombel", 250f, y, paint)
                            canvas.drawText("Jumlah Juz", 330f, y, paint)
                            canvas.drawText("Jumlah Surah", 410f, y, paint)
                            canvas.drawText("Penilaian", 500f, y, paint)
                            y += 20f
                            paint.isFakeBoldText = false
                        }
                        
                        val records = tahfizRecords.filter { it.santriId == santri.id && isDateInRange(it.tanggal, startDate, endDate) }
                        val jmlJuz = records.map { record ->
                            com.example.data.model.QuranData.ALL_JUZ.find { juz -> juz.surahs.any { it.first == record.surahJuz } }?.name ?: ""
                        }.filter { it.isNotEmpty() }.distinct().size
                        val jmlSurah = records.map { it.surahJuz }.distinct().size
                        val penilaian = records.maxByOrNull { it.id }?.nilai ?: "-"
                        
                        canvas.drawText(santri.nama.take(25), 50f, y, paint)
                        canvas.drawText(santri.kelas, 250f, y, paint)
                        canvas.drawText(jmlJuz.toString(), 330f, y, paint)
                        canvas.drawText(jmlSurah.toString(), 410f, y, paint)
                        canvas.drawText(penilaian, 500f, y, paint)
                        y += 20f
                    }
                    pdfDocument.finishPage(page)
                }
                
                context.contentResolver.openOutputStream(uri)?.use { 
                    pdfDocument.writeTo(it)
                }
                pdfDocument.close()
                viewModel.showToast("Berhasil mengekspor PDF")
            } catch (e: Exception) {
                viewModel.showToast("Gagal mengekspor PDF")
            }
        }
    }
    
    val exportExcelLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    OutputStreamWriter(outputStream).use { writer ->
                        writer.write("Nama Santri,Rombel,Jumlah Juz,Jumlah Surah,Penilaian\n")
                        val filteredSantri = if (selectedRombel != null) santriList.filter { it.kelas == selectedRombel } else santriList
                        val santriGroupedByRombel = filteredSantri.groupBy { it.kelas }.toSortedMap()
                        
                        for ((rombel, santris) in santriGroupedByRombel) {
                            for (santri in santris) {
                                val records = tahfizRecords.filter { it.santriId == santri.id && isDateInRange(it.tanggal, startDate, endDate) }
                                val jmlJuz = records.map { record ->
                                    com.example.data.model.QuranData.ALL_JUZ.find { juz -> juz.surahs.any { it.first == record.surahJuz } }?.name ?: ""
                                }.filter { it.isNotEmpty() }.distinct().size
                                val jmlSurah = records.map { it.surahJuz }.distinct().size
                                val penilaian = records.maxByOrNull { it.id }?.nilai ?: "-"
                                
                                writer.write("${santri.nama},${santri.kelas},$jmlJuz,$jmlSurah,$penilaian\n")
                            }
                        }
                    }
                }
                viewModel.showToast("Berhasil mengekspor Excel")
            } catch (e: Exception) {
                viewModel.showToast("Gagal mengekspor Excel")
            }
        }
    }
    
    val filteredSantriList = remember(santriList, selectedRombel, filterSort) {
        val list = if (selectedRombel == null) santriList else santriList.filter { it.kelas == selectedRombel }
        list 
    }

    if (showStartDatePicker) {
        val initialMillis = try { dateFormatter.parse(startDate)?.time } catch (e: Exception) { System.currentTimeMillis() }
        com.example.ui.components.ModernDatePickerDialog(
            initialDateMillis = initialMillis,
            onDismiss = { showStartDatePicker = false },
            onDateSelected = { 
                startDate = dateFormatter.format(Date(it))
                showStartDatePicker = false 
            }
        )
    }

    if (showEndDatePicker) {
        val initialMillis = try { dateFormatter.parse(endDate)?.time } catch (e: Exception) { System.currentTimeMillis() }
        com.example.ui.components.ModernDatePickerDialog(
            initialDateMillis = initialMillis,
            onDismiss = { showEndDatePicker = false },
            onDateSelected = { 
                endDate = dateFormatter.format(Date(it))
                showEndDatePicker = false 
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF064E3B))
                    .border(2.dp, Color(0xFF10B981), RoundedCornerShape(20.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.BarChart, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Rekapitulasi Hafalan",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 18.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF10B981))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "TAHFIZ",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                            Text(
                                text = "Grafik progres & unduh rapor",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            )
                        }
                    }
                }
            }
        }
        
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Download, contentDescription = null, tint = Color(0xFF047857), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Unduh Data Rekapitulasi",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f).clickable { showStartDatePicker = true }) {
                            CustomInputField(
                                value = startDate,
                                onValueChange = {},
                                label = "DARI TANGGAL",
                                placeholder = "dd/MM/yyyy"
                            )
                        }
                        Box(modifier = Modifier.weight(1f).clickable { showEndDatePicker = true }) {
                            CustomInputField(
                                value = endDate,
                                onValueChange = {},
                                label = "SAMPAI TANGGAL",
                                placeholder = "dd/MM/yyyy"
                            )
                        }
                    }

                    CustomDropdown(
                        label = "PILIH ROMBEL UNTUK UNDUH",
                        options = rombelOptions,
                        selectedOption = selectedRombel,
                        onOptionSelected = { selectedRombel = it },
                        optionToString = { it ?: "Semua Rombel" },
                        focusAccentColor = Color(0xFF10B981)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { exportExcelLauncher.launch("rekap_tahfiz_${selectedRombel ?: "semua"}.csv") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.GridOn, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Unduh Excel", fontWeight = FontWeight.Bold)
                        }
                        
                        Button(
                            onClick = { exportPdfLauncher.launch("rekap_tahfiz_${selectedRombel ?: "semua"}.pdf") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Unduh PDF", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            CustomDropdown(
                label = "",
                options = sortOptions,
                selectedOption = filterSort,
                onOptionSelected = { filterSort = it },
                optionToString = { it },
                focusAccentColor = Color(0xFF10B981)
            )
        }

        items(filteredSantriList.take(6)) { indexSantri ->
            val rankIndex = filteredSantriList.indexOf(indexSantri) + 1
            val isComplete = rankIndex <= 2
            val surahTuntas = if (isComplete) 37 else if (rankIndex == 3) 3 else 0
            val belumTuntas = 37 - surahTuntas
            val ketuntasanAyat = if (isComplete) 564 else if (rankIndex == 3) 128 else 0
            val percent = if (isComplete) 100 else if (rankIndex == 3) 23 else 0

            PesantrenCard(accentColor = Color(0xFF10B981)) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFECFDF5))
                                    .border(1.dp, Color(0xFFA7F3D0), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "#$rankIndex",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF047857)
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = indexSantri.nama,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        fontSize = 15.sp
                                    )
                                )
                                Text(
                                    text = "KELAS ${indexSantri.kelas}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.outline)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Belum Setor",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "SURAH TUNTAS ",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                            Text(
                                text = "$surahTuntas",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF047857),
                                    fontSize = 12.sp
                                )
                            )
                            Text(
                                text = "/37",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    fontSize = 11.sp
                                )
                            )
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "BELUM TUNTAS ",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                            Text(
                                text = "$belumTuntas",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (belumTuntas > 0) Color(0xFFEF4444) else Color(0xFF047857),
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFF10B981))
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircleOutline, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Ketuntasan Juz 30 ($ketuntasanAyat / 564 Ayat)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onBackground,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                            Text(
                                text = "$percent%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF047857),
                                    fontSize = 13.sp
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(MaterialTheme.colorScheme.outline)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction = percent / 100f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(0xFF10B981))
                            )
                        }
                    }
                }
            }
        }
    }
}
