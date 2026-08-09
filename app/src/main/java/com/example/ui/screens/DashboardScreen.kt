package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ModuleHeaderBanner
import com.example.ui.components.NavItem
import com.example.ui.theme.Mapel1Accent
import com.example.ui.theme.Mapel2Accent
import com.example.ui.theme.TahfizAccent
import com.example.ui.viewmodel.PesantrenViewModel

@Composable
fun DashboardScreen(
    viewModel: PesantrenViewModel,
    onNavigate: (NavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val santriList by viewModel.santriList.collectAsState()
    val attendanceRecords by viewModel.attendanceRecords.collectAsState()
    val masterSubjects by viewModel.masterSubjects.collectAsState()
    
    val mapel1Name by viewModel.mapel1Name.collectAsState()
    val mapel2Name by viewModel.mapel2Name.collectAsState()
    
    val isTahfizVisible by viewModel.isTahfizVisible.collectAsState()
    val isMapel1Visible by viewModel.isMapel1Visible.collectAsState()
    val isMapel2Visible by viewModel.isMapel2Visible.collectAsState()

    val totalSantri = santriList.size
    val customSubjects = masterSubjects.filter { it.category == "CUSTOM" && it.isVisible }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section H: HERO BANNER (Modified for new look)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF10B981).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Dashboard, contentDescription = null, tint = Color(0xFF10B981))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Dasbor Utama",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    }
                }
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, Color(0xFF10B981).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .clickable { viewModel.showToast("Data Diperbarui") }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Segarkan Data",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
        
        item {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "REKAPITULASI ABSENSI SEMESTER (PER MATA PELAJARAN)",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = Color(0xFF10B981),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )
            }
        }
        
        if (isTahfizVisible) {
            item {
                SubjectAttendanceCard(
                    subjectName = "TAHFIZ",
                    totalSantri = totalSantri,
                    attendanceRecords = attendanceRecords.filter { it.sesi.contains("Tahfiz", ignoreCase = true) },
                    accentColor = Color(0xFF059669) // Green
                )
            }
        }
        
        if (isMapel1Visible) {
            item {
                SubjectAttendanceCard(
                    subjectName = mapel1Name.uppercase(),
                    totalSantri = totalSantri,
                    attendanceRecords = attendanceRecords.filter { it.sesi.equals(mapel1Name, ignoreCase = true) },
                    accentColor = Color(0xFF2563EB) // Blue
                )
            }
        }
        
        if (isMapel2Visible) {
            item {
                SubjectAttendanceCard(
                    subjectName = mapel2Name.uppercase(),
                    totalSantri = totalSantri,
                    attendanceRecords = attendanceRecords.filter { it.sesi.equals(mapel2Name, ignoreCase = true) },
                    accentColor = Color(0xFF9333EA) // Purple
                )
            }
        }
        
val colorPalette = listOf(
            Color(0xFFEA580C), // Orange
            Color(0xFF0D9488), // Teal
            Color(0xFF2563EB), // Blue
            Color(0xFFD97706), // Amber
            Color(0xFF4F46E5), // Indigo
            Color(0xFFDB2777)  // Pink
        )
        
        itemsIndexed(customSubjects) { index, customSub ->
            val accent = colorPalette[index % colorPalette.size]
            SubjectAttendanceCard(
                subjectName = customSub.namaMapel.uppercase(),
                totalSantri = totalSantri,
                attendanceRecords = attendanceRecords.filter { it.sesi.equals(customSub.namaMapel, ignoreCase = true) },
                accentColor = accent
            )
        }
    }
}

@Composable
private fun SubjectAttendanceCard(
    subjectName: String,
    totalSantri: Int,
    attendanceRecords: List<com.example.data.model.AttendanceRecord>,
    accentColor: Color
) {
    val hadirCount = attendanceRecords.count { it.status == "H" }
    val izinCount = attendanceRecords.count { it.status == "I" }
    val sakitCount = attendanceRecords.count { it.status == "S" }
    val alpaCount = attendanceRecords.count { it.status == "A" }
    val telatCount = attendanceRecords.count { it.status == "T" } // Assuming 'T' for Telat, if any
    
    val total = if (attendanceRecords.isNotEmpty()) attendanceRecords.size else 1
    
    val pring = { count: Int -> (count * 100) / total }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
    ) {
        Column {
            // Top Accent Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(accentColor)
            )
            
            Column(modifier = Modifier.padding(20.dp)) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bookmark, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = subjectName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = accentColor,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$totalSantri Santri Aktif",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "PERIODE: JUMAT, 17 JULI 2026 S/D JUMAT, 7 AGUSTUS 2026",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Progress Bars
                AttendanceProgressBar("Hadir", pring(hadirCount), hadirCount, Color(0xFF10B981))
                Spacer(modifier = Modifier.height(16.dp))
                AttendanceProgressBar("Telat", pring(telatCount), telatCount, Color(0xFFFBBF24))
                Spacer(modifier = Modifier.height(16.dp))
                AttendanceProgressBar("Izin", pring(izinCount), izinCount, Color(0xFF3B82F6))
                Spacer(modifier = Modifier.height(16.dp))
                AttendanceProgressBar("Sakit", pring(sakitCount), sakitCount, Color(0xFFF97316))
                Spacer(modifier = Modifier.height(16.dp))
                AttendanceProgressBar("Alpa", pring(alpaCount), alpaCount, Color(0xFFEF4444))
            }
        }
    }
}

@Composable
private fun AttendanceProgressBar(
    label: String,
    percentage: Int,
    count: Int,
    color: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$label ($percentage%)",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
            Text(
                text = "$count",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.outline)
        ) {
            if (percentage > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = (percentage / 100f).coerceIn(0.01f, 1f))
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(color)
                )
            }
        }
    }
}
