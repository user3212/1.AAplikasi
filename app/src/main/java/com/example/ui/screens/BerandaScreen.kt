package com.example.ui.screens
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.asImageBitmap

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource

import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.PesantrenViewModel

@Composable
fun BerandaScreen(
    viewModel: PesantrenViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    
    var activeSemester by remember { mutableStateOf(sharedPrefs.getString("active_semester", "Semester Ganjil") ?: "Semester Ganjil") }
    var semesterExpanded by remember { mutableStateOf(false) }
    
    var academicYear by remember { mutableStateOf(sharedPrefs.getString("active_academic_year", "2026/2027") ?: "2026/2027") }
    var yearExpanded by remember { mutableStateOf(false) }
    
    var showAddSemesterDialog by remember { mutableStateOf(false) }
    var newSemesterName by remember { mutableStateOf("") }
    
    var showAddYearDialog by remember { mutableStateOf(false) }
    var newYearName by remember { mutableStateOf("") }
    
    // We store the list of semesters and years in SharedPreferences as well
    val semesterSet = sharedPrefs.getStringSet("semesters", setOf("Semester Ganjil", "Semester Genap"))?.toList() ?: listOf("Semester Ganjil", "Semester Genap")
    val yearSet = sharedPrefs.getStringSet("academic_years", setOf("2025/2026", "2026/2027", "2027/2028"))?.toList() ?: listOf("2025/2026", "2026/2027", "2027/2028")
    
    var semesters by remember { mutableStateOf(semesterSet) }
    var academicYears by remember { mutableStateOf(yearSet) }

    fun updateDatabaseConfig(sem: String, year: String) {
        sharedPrefs.edit()
            .putString("active_semester", sem)
            .putString("active_academic_year", year)
            .apply()
        // Notify the user they need to restart or sync to load the new database
        viewModel.showToast("Konfigurasi diubah ke $sem $year. Silakan klik Sinkronisasi.")
    }

    if (showAddSemesterDialog) {
        AlertDialog(
            onDismissRequest = { showAddSemesterDialog = false },
            title = { Text("Tambah Semester", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newSemesterName,
                    onValueChange = { newSemesterName = it },
                    label = { Text("Nama Semester") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = { 
                    if (newSemesterName.isNotBlank()) {
                        val newSemesters = semesters.toMutableList().apply { add(newSemesterName) }
                        semesters = newSemesters
                        sharedPrefs.edit().putStringSet("semesters", newSemesters.toSet()).apply()
                    }
                    showAddSemesterDialog = false 
                }) {
                    Text("Simpan", color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSemesterDialog = false }) {
                    Text("Batal", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                }
            }
        )
    }

    if (showAddYearDialog) {
        AlertDialog(
            onDismissRequest = { showAddYearDialog = false },
            title = { Text("Tambah Tahun Ajaran", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newYearName,
                    onValueChange = { newYearName = it },
                    label = { Text("Tahun Ajaran (Contoh: 2028/2029)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = { 
                    if (newYearName.isNotBlank()) {
                        val newYears = academicYears.toMutableList().apply { add(newYearName) }
                        academicYears = newYears
                        sharedPrefs.edit().putStringSet("academic_years", newYears.toSet()).apply()
                    }
                    showAddYearDialog = false 
                }) {
                    Text("Simpan", color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddYearDialog = false }) {
                    Text("Batal", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                }
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
        // Section 1: HERO BANNER (AHLAN WA SAHLAN)
        item {
            HeroBerandaBanner(viewModel)
        }

        // Section 2: KONFIGURASI SEMESTER & DATABASE
        item {
            Surface(
color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f)),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFD1FAE5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Konfigurasi",
                                tint = Color(0xFF059669),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Konfigurasi Semester & Database",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 17.sp
                                )
                            )
                            Text(
                                text = "Pilih semester dan tahun ajaran untuk mengelola data.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    // Form controls row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Dropdown 1: SEMESTER AKTIF
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "SEMESTER AKTIF",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF047857),
                                        fontSize = 11.sp
                                    ),
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                                Text(
                                    text = "+ Tambah",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF3B82F6),
                                        fontSize = 11.sp
                                    ),
                                    modifier = Modifier
                                        .padding(bottom = 6.dp)
                                        .clickable { showAddSemesterDialog = true }
                                )
                            }
                            Box {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surface)
                                        .clickable { semesterExpanded = true }
                                        .padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = activeSemester,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onBackground,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 13.sp
                                        )
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Dropdown",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                DropdownMenu(
                                    expanded = semesterExpanded,
                                    onDismissRequest = { semesterExpanded = false }
                                ) {
                                    semesters.forEach { sem ->
                                        DropdownMenuItem(
                                            text = { Text(sem) },
                                            onClick = {
                                                activeSemester = sem
                                                semesterExpanded = false
                                                updateDatabaseConfig(sem, academicYear)
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Dropdown 2: TAHUN AJARAN
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "TAHUN AJARAN",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF047857),
                                        fontSize = 11.sp
                                    ),
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                                Text(
                                    text = "+ Tambah",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF3B82F6),
                                        fontSize = 11.sp
                                    ),
                                    modifier = Modifier
                                        .padding(bottom = 6.dp)
                                        .clickable { showAddYearDialog = true }
                                )
                            }
                            Box {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surface)
                                        .clickable { yearExpanded = true }
                                        .padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = academicYear,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onBackground,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 13.sp
                                        )
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Dropdown",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                DropdownMenu(
                                    expanded = yearExpanded,
                                    onDismissRequest = { yearExpanded = false }
                                ) {
                                    academicYears.forEach { yr ->
                                        DropdownMenuItem(
                                            text = { Text(yr) },
                                            onClick = {
                                                academicYear = yr
                                                yearExpanded = false
                                                updateDatabaseConfig(activeSemester, yr)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Sinkronisasi Button
                    Button(
                        onClick = {
                            viewModel.showToast("Database disinkronkan. Menggunakan database $activeSemester TA $academicYear.")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1D90AD),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_sinkronisasi")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Sync",
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Sinkronisasi Data",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // Info Callout Box
                    Surface(
                        color = Color(0xFF1DAD82),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Info",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Text(
                                text = "Data akan disimpan pada database offline terpisah per semester dan tahun ajaran untuk mencegah penumpukan data.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeroBerandaBanner(viewModel: PesantrenViewModel) {
    val profilBase64 by viewModel.profilBase64.collectAsState()
    val namaGuruRaw by viewModel.namaGuru.collectAsState()
    val jenisKelamin by viewModel.jenisKelamin.collectAsState()
    val namaGuru = if (!namaGuruRaw.isNullOrBlank()) namaGuruRaw else "Guru"
    val sapaan = if (jenisKelamin == "Perempuan") "Ibu" else "Bapak"
    val namaSapaan = "$sapaan $namaGuru"

    var currentTime by remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while(true) {
            currentTime = LocalDateTime.now()
            delay(1000)
        }
    }

    val hour = currentTime.hour
    val greeting = when {
        hour in 5..10 -> "Selamat Pagi,"
        hour in 11..14 -> "Selamat Siang,"
        hour in 15..17 -> "Selamat Sore,"
        else -> "Selamat Malam,"
    }

    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale("id", "ID"))
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale("id", "ID"))
    
    val timeString = currentTime.format(timeFormatter)
    val dateString = currentTime.format(dateFormatter)

    Surface(
        color = Color.Transparent,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 12.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF047857),
                            Color(0xFF064E3B)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Greeting & Name
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        Text(
                            text = greeting,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        )
                        Text(
                            text = namaSapaan,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 32.sp,
                                lineHeight = 36.sp,
                                shadow = androidx.compose.ui.graphics.Shadow(
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                    offset = androidx.compose.ui.geometry.Offset(2f, 4f),
                                    blurRadius = 4f
                                )
                            )
                        )
                        Text(
                            text = "Selamat datang di aplikasi Guruqu",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = Color(0xFFD1FAE5),
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                // Digital Clock and Date Container
                Surface(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val isDarkMode by viewModel.isDarkMode.collectAsState()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF0F766E).copy(alpha = 0.9f),
                                        Color(0xFF047857).copy(alpha = 0.9f)
                                    )
                                )
                            )
                            .border(1.dp, Color(0xFF6EE7B7).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(vertical = 20.dp, horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            // Dark Mode Toggle
                            Box(
                                modifier = Modifier
                                    .height(44.dp) // Matching time text height approximately
                                    .width(28.dp)
                                    .shadow(6.dp, RoundedCornerShape(14.dp))
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            colors = if (isDarkMode) listOf(MaterialTheme.colorScheme.onBackground, MaterialTheme.colorScheme.onBackground)
                                                    else listOf(Color(0xFF93C5FD), Color(0xFF3B82F6))
                                        )
                                    )
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        viewModel.toggleDarkMode()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                val thumbOffset by animateFloatAsState(
                                    targetValue = if (isDarkMode) -10f else 10f,
                                    animationSpec = tween(300),
                                    label = "thumbOffset"
                                )
                                // Track icons
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(vertical = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Icon(Icons.Default.DarkMode, contentDescription = null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(12.dp))
                                    Icon(Icons.Default.LightMode, contentDescription = null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(12.dp))
                                }
                                // Thumb
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .offset(y = thumbOffset.dp)
                                        .shadow(4.dp, CircleShape)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                        contentDescription = null,
                                        tint = if (isDarkMode) MaterialTheme.colorScheme.onBackground else Color(0xFF3B82F6),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = timeString,
                                    style = MaterialTheme.typography.displayMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 36.sp,
                                        letterSpacing = 2.sp,
                                        shadow = androidx.compose.ui.graphics.Shadow(
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                                            offset = androidx.compose.ui.geometry.Offset(0f, 4f),
                                            blurRadius = 8f
                                        )
                                    )
                                )
                                Text(
                                    text = dateString,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color(0xFFA7F3D0),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp
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

@Composable
fun RiayahEmblemLogo(modifier: Modifier = Modifier) {
    Surface(
color = MaterialTheme.colorScheme.surface,
        shape = CircleShape,
        shadowElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(4.dp, Color(0xFF059669)),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Gold Ring accent inside
            Canvas(modifier = Modifier.fillMaxSize(0.92f)) {
                drawCircle(
                    color = Color(0xFFD97706),
                    style = Stroke(width = 3.dp.toPx())
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(12.dp)
            ) {
                // Mosque Crescent & Dome Icon Graphic
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF047857)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(32.dp)) {
                        // Draw Dome Path
                        val domePath = Path().apply {
                            moveTo(size.width * 0.2f, size.height * 0.8f)
                            lineTo(size.width * 0.8f, size.height * 0.8f)
                            quadraticTo(size.width * 0.8f, size.height * 0.4f, size.width * 0.5f, size.height * 0.2f)
                            quadraticTo(size.width * 0.2f, size.height * 0.4f, size.width * 0.2f, size.height * 0.8f)
                            close()
                        }
                        drawPath(path = domePath, color = Color(0xFFFDE047))

                        // Draw Crescent Star Accent
                        drawCircle(
                            color = Color.White,
                            radius = size.width * 0.08f,
                            center = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.18f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "RI'AYAH",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color(0xFF065F46),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        letterSpacing = 1.sp
                    )
                )

                Text(
                    text = "CLASS & SANTRI\nMANAGEMENT SYSTEM",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFFD97706),
                        fontWeight = FontWeight.Bold,
                        fontSize = 7.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 9.sp
                    )
                )
            }
        }
    }
}
