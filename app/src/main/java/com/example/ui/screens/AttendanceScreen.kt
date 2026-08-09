package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CoPresent
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
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
import com.example.ui.components.CustomDropdown
import com.example.ui.components.CustomInputField
import com.example.ui.theme.*
import com.example.ui.viewmodel.PesantrenViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



@Composable
fun AttendanceScreen(
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
        val sesi = "Tahfiz"
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
        val sesi = "Tahfiz"
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
        // TOP ROYAL NAVY BANNER (Changed to Tahfiz Green)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF064E3B), Color(0xFF047857))
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
                                    text = "Absensi Santri",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 18.sp
                                    )
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF10B981).copy(alpha = 0.3f))
                                        .border(1.dp, Color(0xFFA7F3D0).copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "TAHFIZ",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFFA7F3D0),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Kehadiran Harian Tahfiz",
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
                            .background(Color(0xFF059669))
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
                                tint = Color(0xFF059669),
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
                                        .background(Color(0xFFD1FAE5))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = absensiRombel,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF047857),
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
                            focusAccentColor = Color(0xFF059669),
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
                                focusedBorderColor = Color(0xFF059669),
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
                            color = if (absensiTab == 0) Color(0xFF047857) else MaterialTheme.colorScheme.onSurfaceVariant,
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
                            color = if (absensiTab == 1) Color(0xFF047857) else MaterialTheme.colorScheme.onSurfaceVariant,
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
                        .background(Color(0xFF064E3B))
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
                                    .background(Color(0xFFD1FAE5)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FilterAlt,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
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
                        .background(Color(0xFF10B981))
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
                                    sesi = "Tahfiz",
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
                currentDate = dateFormatter.format(java.util.Date(millis))
            },
            onDismiss = { showDatePickerDialog = false }
        )
    }
}
