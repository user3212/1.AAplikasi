package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import java.io.OutputStreamWriter
import androidx.compose.ui.platform.LocalContext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CustomDropdown
import com.example.ui.components.PesantrenCard
import com.example.ui.viewmodel.PesantrenViewModel

data class HistorySession(
    val date: String,
    val topic: String
)

data class StudentRekapRow(
    val name: String,
    val hadir: Int,
    val telat: Int,
    val izin: Int,
    val sakit: Int,
    val alpa: Int,
    val date: String,
    val topic: String,
    val score: Int
)

@Composable
fun ExportBackupScreen(
    viewModel: PesantrenViewModel,
    modifier: Modifier = Modifier
) {
    val santriList by viewModel.santriList.collectAsState()
    val gradeRecords by viewModel.gradeRecords.collectAsState()
    val attendanceRecords by viewModel.attendanceRecords.collectAsState()
    val masterClasses by viewModel.masterClasses.collectAsState()
    val masterSubjects by viewModel.masterSubjects.collectAsState()

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    var tempCsvContent by remember { mutableStateOf("") }
    val exportExcelLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri: Uri? ->
        if (uri != null) {
            viewModel.showLoadingOverlay("Mengekspor data Excel...")
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    OutputStreamWriter(outputStream).use { writer ->
                        writer.write(tempCsvContent)
                    }
                }
            } catch (e: Exception) {
                viewModel.showToast("Gagal mengekspor file")
            }
        }
    }


    // Rekap Selection State
    var selectedMapel by remember { mutableStateOf("Mapel Informatika") }
    var selectedRombel by remember { mutableStateOf("7A") }
    var isViewActive by remember { mutableStateOf(true) }
    var currentHistoryIndex by remember { mutableIntStateOf(0) }

    val mapelOptions = remember(masterSubjects) { listOf("Mapel Informatika", "Mapel SKI", "Tahfiz Al-Qur'an") + masterSubjects.map { it.namaMapel } }.distinct()
    
    val rombelOptions = remember(masterClasses, santriList) {
        val classNames = masterClasses.map { it.namaKelas.replace("Kelas ", "").trim() }.filter { it.isNotBlank() }
        val santriClasses = santriList.map { it.kelas.replace("Kelas ", "").trim() }.filter { it.isNotBlank() }
        val combined = (listOf("7A", "7B", "8A", "9A") + classNames + santriClasses).distinct().sorted()
        listOf("Pilih Rombel...") + combined + listOf("Semua Rombel")
    }

    // Default history dates for preview history tracking
    val historySessions = remember(selectedMapel, gradeRecords, attendanceRecords) {
        val gradeDates = gradeRecords.filter { it.namaMapel.contains(selectedMapel, ignoreCase = true) || selectedMapel.contains(it.namaMapel, ignoreCase = true) }.map { HistorySession(it.tanggal, it.jenisUjian) }
        val attendanceDates = attendanceRecords.map { HistorySession(it.tanggal, it.sesi) }
        val allDates = (gradeDates + attendanceDates).distinctBy { it.date }.sortedByDescending { it.date }
        if (allDates.isNotEmpty()) allDates else listOf(HistorySession("2026-08-05", "PBM"))
    }

    val safeHistoryIndex = currentHistoryIndex.coerceIn(0, (historySessions.size - 1).coerceAtLeast(0))
    val activeSession = historySessions.getOrNull(safeHistoryIndex) ?: HistorySession("2026-08-05", "PBM")

    // Dynamic Database calculations combined with fallback
    val displayTableRows = remember(santriList, gradeRecords, attendanceRecords, selectedRombel, selectedMapel, activeSession) {
        val activeHalqah = when {
            selectedMapel.contains("SKI", ignoreCase = true) -> "MAPEL1"
            selectedMapel.contains("Informatika", ignoreCase = true) -> "MAPEL2"
            else -> {
                val subject = masterSubjects.find { it.namaMapel == selectedMapel }
                if (subject?.category == "CUSTOM") {
                    "CUSTOM_${selectedMapel}"
                } else {
                    subject?.category ?: "UMUM"
                }
            }
        }
        
        val cleanRombel = selectedRombel.replace("Kelas ", "").trim()
        val filteredSantri = santriList.filter { s ->
            val matchRombel = if (selectedRombel == "Semua Rombel") true else s.kelas.replace("Kelas ", "").trim().equals(cleanRombel, ignoreCase = true)
            val matchHalqah = (s.halqah == activeHalqah)
            matchRombel && matchHalqah
        }

        if (filteredSantri.isNotEmpty()) {
            filteredSantri.map { s ->
                val santriAttendance = attendanceRecords.filter { it.santriId == s.id || it.santriNama.equals(s.nama, ignoreCase = true) }
                val hadir = santriAttendance.count { it.status == "H" }
                val telat = santriAttendance.count { it.status == "T" }
                val izin = santriAttendance.count { it.status == "I" }
                val sakit = santriAttendance.count { it.status == "S" }
                val alpa = santriAttendance.count { it.status == "A" }

                val santriGrade = gradeRecords.find { g ->
                    (g.santriId == s.id || g.santriNama.equals(s.nama, ignoreCase = true)) &&
                    (g.namaMapel.contains(selectedMapel, ignoreCase = true) || selectedMapel.contains(g.namaMapel, ignoreCase = true))
                }

                val score = santriGrade?.nilai?.toInt() ?: 80
                val topic = santriGrade?.jenisUjian ?: activeSession.topic
                val date = santriGrade?.tanggal ?: activeSession.date

                StudentRekapRow(
                    name = s.nama,
                    hadir = if (santriAttendance.isNotEmpty()) hadir else 3,
                    telat = telat,
                    izin = izin,
                    sakit = sakit,
                    alpa = alpa,
                    date = date,
                    topic = topic,
                    score = score
                )
            }
        } else {
            emptyList()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // TOP GREEN BANNER CARD (Matching Screenshot 1 & 3)
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF064E3B),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF059669).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF064E3B), Color(0xFF047857), Color(0xFF0F766E))
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Glassmorphism XLS Icon Container
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.TableChart,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "XLS",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Laporan Rekap Data",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 20.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Rekapitulasi PBM & Ekspor Excel",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // FILTER SELECTION CARD (MATA PELAJARAN & PILIH ROMBEL)
        item {
            PesantrenCard(accentColor = Color(0xFF059669)) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Mata Pelajaran Dropdown Field
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "MATA PELAJARAN",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 0.5.sp,
                                fontSize = 11.sp
                            )
                        )
                    }

                    CustomDropdown(
                        label = "",
                        options = mapelOptions,
                        selectedOption = selectedMapel,
                        onOptionSelected = { selectedMapel = it },
                        optionToString = { it },
                        focusAccentColor = Color(0xFF059669)
                    )

                    // Pilih Rombel Dropdown Field
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = null,
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PILIH ROMBEL",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 0.5.sp,
                                fontSize = 11.sp
                            )
                        )
                    }

                    CustomDropdown(
                        label = "",
                        options = rombelOptions,
                        selectedOption = selectedRombel,
                        onOptionSelected = { selectedRombel = it },
                        optionToString = { it },
                        focusAccentColor = Color(0xFF059669)
                    )

                    // Action Buttons Row (Lihat & Unduh)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Button 1: Lihat
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.outline)
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                                .clickable {
                                    if (selectedRombel == "Pilih Rombel...") {
                                        viewModel.showToast("Silakan pilih Rombel terlebih dahulu!")
                                    } else {
                                        isViewActive = true
                                        viewModel.showToast("Menampilkan Rekap $selectedMapel - Rombel $selectedRombel")
                                    }
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Lihat",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = MaterialTheme.colorScheme.onBackground,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                )
                            }
                        }

                        // Button 2: Unduh
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF059669))
                                .clickable {
                                    if (selectedRombel == "Pilih Rombel...") {
                                        viewModel.showToast("Silakan pilih Rombel sebelum mengunduh Excel!")
                                    } else {
                                        val csvContent = buildString {
                                            appendLine("REKAPITULASI DATA PESANTRENQU")
                                            appendLine("Mata Pelajaran: $selectedMapel")
                                            appendLine("Rombel: $selectedRombel")
                                            appendLine("Tanggal Rekap: ${activeSession.date}")
                                            appendLine()
                                            appendLine("NO,NAMA SISWA,HADIR,TELAT,IZIN,SAKIT,ALPA,TANGGAL,MATERI,NILAI")
                                            displayTableRows.forEachIndexed { index, row ->
                                                appendLine("${index + 1},\"${row.name}\",${row.hadir},${row.telat},${row.izin},${row.sakit},${row.alpa},\"${row.date}\",\"${row.topic}\",${row.score}")
                                            }
                                        }
                                        tempCsvContent = csvContent
                                        exportExcelLauncher.launch("rekap_${selectedMapel.replace(" ", "_")}_${selectedRombel}.csv")
                                    }
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.FileDownload,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Unduh",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // MONITOR PRATINJAU REKAP CARD
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Monitor Title Header with History Date Switcher
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Title Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Computer,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "MONITOR PRATINJAU REKAP",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    letterSpacing = 0.5.sp,
                                    fontSize = 13.sp
                                ),
                                maxLines = 1,
                                softWrap = false
                            )
                        }

                        // History Navigation Bar
                        if (isViewActive && selectedRombel != "Pilih Rombel...") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Previous Date History Button
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.outline)
                                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                        .clickable {
                                            if (currentHistoryIndex > 0) {
                                                currentHistoryIndex--
                                            } else {
                                                viewModel.showToast("Sudah di data history awal")
                                            }
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "Sebelumnya",
                                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold, fontSize = 11.sp),
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    }
                                }

                                // Date History Badge Pill
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFDCFCE7))
                                        .border(1.dp, Color(0xFF86EFAC), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "${safeHistoryIndex + 1}/${historySessions.size}: ${activeSession.date}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF065F46),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 11.sp
                                        ),
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }

                                // Next Date History Button
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.outline)
                                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                        .clickable {
                                            if (currentHistoryIndex < historySessions.size - 1) {
                                                currentHistoryIndex++
                                            } else {
                                                viewModel.showToast("Sudah di data history terbaru")
                                            }
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Selanjutnya",
                                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold, fontSize = 11.sp),
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    // Content Area: Empty State OR Full Interactive Data Table
                    if (!isViewActive || selectedRombel == "Pilih Rombel...") {
                        // EMPTY PLACEHOLDER STATE (Matching Screenshot 3)
                        Column {
                            // Dark Slate Sub-Header
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = "SILAKAN PILIH ROMBEL & KLIK LIHAT...",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp,
                                        fontSize = 11.sp
                                    )
                                )
                            }

                            // Centered Placeholder Icon and Text
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(MaterialTheme.colorScheme.outline),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TableChart,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "SILAKAN PILIH MAPEL DAN ROMBEL, LALU KLIK LIHAT.",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.5.sp,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center
                                    )
                                )
                            }
                        }
                    } else {
                        // FULL DATA TABLE (Matching Screenshots 1 & 2)
                        val horizontalScrollState = rememberScrollState()

                        Box(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
                            Column(modifier = Modifier.width(760.dp)) {
                                // Dark Slate Double Header Row
                                Surface(color = MaterialTheme.colorScheme.onBackground) {
                                    Column {
                                        // Header Row Level 1: Category Groups
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "NAMA SISWA",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp
                                                ),
                                                modifier = Modifier
                                                    .width(180.dp)
                                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                                            )

                                            // Absensi Total Group
                                            Box(
                                                modifier = Modifier
                                                    .width(220.dp)
                                                    .padding(vertical = 6.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "ABSENSI TOTAL",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = Color.White,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        fontSize = 11.sp
                                                    )
                                                )
                                            }

                                            // Penilaian Harian Group
                                            Box(
                                                modifier = Modifier
                                                    .width(360.dp)
                                                    .padding(vertical = 6.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "PENILAIAN HARIAN",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = Color.White,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        fontSize = 11.sp
                                                    )
                                                )
                                            }
                                        }

                                        HorizontalDivider(color = MaterialTheme.colorScheme.onBackground, thickness = 1.dp)

                                        // Header Row Level 2: Specific Sub-Columns
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Spacer(modifier = Modifier.width(180.dp))

                                            // Sub-columns for Absensi Total
                                            Row(modifier = Modifier.width(220.dp), horizontalArrangement = Arrangement.SpaceAround) {
                                                Text("HADIR", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF34D399), fontWeight = FontWeight.Bold, fontSize = 9.sp))
                                                Text("TELAT", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold, fontSize = 9.sp))
                                                Text("IZIN", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF60A5FA), fontWeight = FontWeight.Bold, fontSize = 9.sp))
                                                Text("SAKIT", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFFB923C), fontWeight = FontWeight.Bold, fontSize = 9.sp))
                                                Text("ALPA", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFF87171), fontWeight = FontWeight.Bold, fontSize = 9.sp))
                                            }

                                            // Sub-columns for Penilaian Harian
                                            Row(modifier = Modifier.width(360.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Text("TANGGAL", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), fontWeight = FontWeight.Bold, fontSize = 9.sp), modifier = Modifier.width(90.dp).padding(start = 12.dp))
                                                Text("MATERI", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), fontWeight = FontWeight.Bold, fontSize = 9.sp), modifier = Modifier.width(200.dp))
                                                Text("NILAI", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF60A5FA), fontWeight = FontWeight.Bold, fontSize = 9.sp), modifier = Modifier.width(70.dp).padding(end = 12.dp), textAlign = TextAlign.End)
                                            }
                                        }
                                    }
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                                // Dynamic Table Rows
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    displayTableRows.forEachIndexed { index, student ->
                                        val isEven = index % 2 == 0
                                        val rowBg = if (isEven) Color.White else Color(0xFFF8FAFC)

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(rowBg)
                                                .padding(vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Student Name
                                            Text(
                                                text = student.name,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onBackground,
                                                    fontSize = 12.sp,
                                                    lineHeight = 14.sp
                                                ),
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier
                                                    .width(180.dp)
                                                    .padding(horizontal = 12.dp)
                                            )

                                            // Absensi Total Columns
                                            Row(
                                                modifier = Modifier.width(220.dp),
                                                horizontalArrangement = Arrangement.SpaceAround,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(text = "${student.hadir}", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF059669), fontWeight = FontWeight.Bold, fontSize = 12.sp))
                                                Text(text = "${student.telat}", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFD97706), fontWeight = FontWeight.Bold, fontSize = 12.sp))
                                                Text(text = "${student.izin}", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF2563EB), fontWeight = FontWeight.Bold, fontSize = 12.sp))
                                                Text(text = "${student.sakit}", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFEA580C), fontWeight = FontWeight.Bold, fontSize = 12.sp))
                                                Text(text = "${student.alpa}", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFDC2626), fontWeight = FontWeight.Bold, fontSize = 12.sp))
                                            }

                                            // Penilaian Harian Columns
                                            Row(
                                                modifier = Modifier.width(360.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = student.date,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        fontSize = 11.sp
                                                    ),
                                                    modifier = Modifier.width(90.dp).padding(start = 12.dp)
                                                )
                                                Text(
                                                    text = student.topic,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = MaterialTheme.colorScheme.onBackground,
                                                        fontWeight = FontWeight.Medium,
                                                        fontSize = 11.sp
                                                    ),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.width(200.dp)
                                                )
                                                Text(
                                                    text = "${student.score}",
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = Color(0xFF1D4ED8),
                                                        fontWeight = FontWeight.ExtraBold,
                                                        fontSize = 13.sp
                                                    ),
                                                    modifier = Modifier.width(70.dp).padding(end = 12.dp),
                                                    textAlign = TextAlign.End
                                                )
                                            }
                                        }

                                        HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
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
