package com.example.ui.components
import androidx.compose.ui.graphics.asImageBitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenu
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.asPaddingValues

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.PesantrenViewModel
import android.content.Context
import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.Add


enum class NavItem(val title: String, val subtitle: String, val icon: ImageVector, val accentColor: Color) {
    BERANDA("Beranda", "Ringkasan Aktivitas", Icons.Default.Home, Color(0xFF8B5CF6)),
    DASHBOARD("Dashboard", "Statistik Utama", Icons.Default.Dashboard, Color(0xFF3B82F6)),
    SANTRI("Data Santri", "Manajemen Santri", Icons.Default.People, Color(0xFFF59E0B)),
    ABSENSI("Absensi Santri", "Presensi Harian", Icons.Default.CheckCircle, Color(0xFF059669)),
    TAHFIZ("Setoran Hafalan", "Catatan Hafalan", Icons.Default.Book, Color(0xFF059669)),
    EVALUASI_TAHFIZ("Evaluasi Kualitas", "Evaluasi Hafalan", Icons.Default.Star, Color(0xFF059669)),
    REKAP_TAHFIZ("Rekap Tahfiz", "Rekapitulasi Santri", Icons.Default.BarChart, Color(0xFF059669)),
    KONFIG_TAHFIZ("Konfigurasi Tahfiz", "Konfigurasi Data", Icons.Default.Settings, Color(0xFF059669)),
    MAPEL1_ROMBEL("Kelola Rombel (SKI)", "Rombel SKI", Icons.Default.Group, Color(0xFF2563EB)),
    MAPEL1_ABSENSI("Absensi Kelas (SKI)", "Absensi SKI", Icons.Default.Schedule, Color(0xFF2563EB)),
    MAPEL1("Penilaian Harian (SKI)", "Penilaian SKI", Icons.Default.School, Color(0xFF2563EB)),
    MAPEL1_UTS("Penilaian UTS (SKI)", "Penilaian SKI - UTS", Icons.Default.Assignment, Color(0xFF2563EB)),
    MAPEL1_PAS("Penilaian PAS (SKI)", "Penilaian SKI - PAS", Icons.Default.Assignment, Color(0xFF2563EB)),
    MAPEL2_ROMBEL("Kelola Rombel (Informatika)", "Rombel Informatika", Icons.Default.Group, Color(0xFF7C3AED)),
    MAPEL2_ABSENSI("Absensi Kelas (Informatika)", "Absensi Informatika", Icons.Default.Schedule, Color(0xFF7C3AED)),
    MAPEL2("Penilaian Harian (Informatika)", "Penilaian Informatika", Icons.Default.Assignment, Color(0xFF7C3AED)),
    MAPEL2_UTS("Penilaian UTS (Informatika)", "Penilaian Informatika - UTS", Icons.Default.Assignment, Color(0xFF7C3AED)),
    MAPEL2_PAS("Penilaian PAS (Informatika)", "Penilaian Informatika - PAS", Icons.Default.Assignment, Color(0xFF7C3AED)),
    EXPORT("Rekap Data (Excel)", "Backup & Laporan", Icons.Default.TableChart, Color(0xFF0D9488)),
    SETTINGS("Fitur Tambahan", "Pengaturan App", Icons.Default.GridOn, Color(0xFF90AD1D)),
    JADWAL("Atur Jadwal", "Jadwal Mengajar", Icons.Default.Schedule, Color(0xFFEAB308)),
    DATABASE("Database", "Manajemen Database", Icons.Default.Storage, Color(0xFFEF4444))
}

@Composable
fun SidebarContent(
    selectedItem: NavItem,
    onItemSelected: (NavItem) -> Unit,
    viewModel: PesantrenViewModel,
    onProfileClick: () -> Unit,
    isProfileVerified: Boolean
) {
    val mapel1Name by viewModel.mapel1Name.collectAsState()
    val mapel2Name by viewModel.mapel2Name.collectAsState()
    val isTahfizVisible by viewModel.isTahfizVisible.collectAsState()
    val isMapel1Visible by viewModel.isMapel1Visible.collectAsState()
    val isMapel2Visible by viewModel.isMapel2Visible.collectAsState()
    val masterSubjects by viewModel.masterSubjects.collectAsState()
    val customSubjects = masterSubjects.filter { it.category == "CUSTOM" && it.isVisible }
    val activeCustomNav by viewModel.activeCustomSubjectNav.collectAsState()

    var tahfizExpanded by remember { mutableStateOf(false) }
    var pbmExpanded by remember { mutableStateOf(false) }
    var skiExpanded by remember { mutableStateOf(false) }
    var infoExpanded by remember { mutableStateOf(false) }
    val namaSekolahState by viewModel.namaSekolah.collectAsState()
    val alamatSekolahState by viewModel.alamatSekolah.collectAsState()
    val profilBase64 by viewModel.profilBase64.collectAsState()
    
    val displayNamaSekolah = namaSekolahState.takeIf { it.isNotBlank() } ?: "Pesantrenqu"
    val displayAlamatSekolah = alamatSekolahState.takeIf { it.isNotBlank() }?.uppercase() ?: "SUBULUS SALAM"

    var setupExpanded by remember { mutableStateOf(true) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // App Logo/Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp), // Padatkan ruang
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Profile Icon with 3D effect and Checkmark/Red X badge
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clickable { onProfileClick() }
                ) {
                    // Main 3D Icon Background
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center)
                            .shadow(6.dp, RoundedCornerShape(10.dp))
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF34D399), Color(0xFF047857))
                                )
                            )
                            .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        val cleanBase64 = if (profilBase64.contains(",")) profilBase64.substringAfter(",") else profilBase64
                        val imageBytes = if (cleanBase64.isNotBlank()) try { android.util.Base64.decode(cleanBase64, android.util.Base64.DEFAULT) } catch(e: Exception) { null } else null
                        val bitmap = if (imageBytes != null) try { android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size) } catch(e: Exception) { null } else null
                        if (bitmap != null) {
                            androidx.compose.foundation.Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Profile Picture",
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                        }
                    }
                    
                    // Small Professional Badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 2.dp, y = (-2).dp)
                            .size(16.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(if (isProfileVerified) Color(0xFF10B981) else Color(0xFFEF4444))
                            .border(1.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isProfileVerified) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = if (isProfileVerified) "Verified" else "Unverified",
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp)) // Padatkan jarak
Column {
                    Text(
                        text = displayNamaSekolah,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground,
                            lineHeight = 20.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = displayAlamatSekolah,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            lineHeight = 12.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                SidebarMainPillButton("BERANDA", Icons.Default.Home, Color(0xFF8B5CF6), selectedItem == NavItem.BERANDA) { onItemSelected(NavItem.BERANDA) }
            }
            item {
                SidebarMainPillButton("DASBOR UTAMA", Icons.Default.Dashboard, Color(0xFFF97316), selectedItem == NavItem.DASHBOARD) { onItemSelected(NavItem.DASHBOARD) }
            }
            
            if (isTahfizVisible) {
                item {
                    SidebarGroupHeaderPill("DATA TAHFIZ", Icons.Default.Book, Color(0xFF10B981), tahfizExpanded) { tahfizExpanded = !tahfizExpanded }
                }
                item {
                    AnimatedVisibility(visible = tahfizExpanded) {
                        Column(modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 8.dp)) {
                            SidebarSubItemRow("Manajemen Santri", Icons.Default.People, selectedItem == NavItem.SANTRI) { onItemSelected(NavItem.SANTRI) }
                            SidebarSubItemRow("Kehadiran Harian", Icons.Default.CheckCircle, selectedItem == NavItem.ABSENSI) { onItemSelected(NavItem.ABSENSI) }
                            SidebarSubItemRow("Setoran Hafalan", Icons.Default.Book, selectedItem == NavItem.TAHFIZ) { onItemSelected(NavItem.TAHFIZ) }
                            SidebarSubItemRow("Evaluasi Kualitas", Icons.Default.Star, selectedItem == NavItem.EVALUASI_TAHFIZ) { onItemSelected(NavItem.EVALUASI_TAHFIZ) }
                            SidebarSubItemRow("Rekapitulasi", Icons.Default.BarChart, selectedItem == NavItem.REKAP_TAHFIZ) { onItemSelected(NavItem.REKAP_TAHFIZ) }
                            SidebarSubItemRow("Konfigurasi", Icons.Default.Settings, selectedItem == NavItem.KONFIG_TAHFIZ) { onItemSelected(NavItem.KONFIG_TAHFIZ) }
                        }
                    }
                }
            }

            if (isMapel1Visible || isMapel2Visible || customSubjects.isNotEmpty()) {
                item {
                    SidebarGroupHeaderPill("PBM KELAS", Icons.Default.School, Color(0xFF3B82F6), pbmExpanded) { pbmExpanded = !pbmExpanded }
                }
                
                item {
                    AnimatedVisibility(visible = pbmExpanded) {
                        Column(modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 8.dp)) {
                            if (isMapel1Visible) {
                                SidebarNestedHeaderRow(mapel1Name, Icons.Default.Book, skiExpanded) { skiExpanded = !skiExpanded }
                                AnimatedVisibility(visible = skiExpanded) {
                                    Column(modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 8.dp)) {
                                        SidebarSubItemRow("Kelola Rombel", Icons.Default.Group, selectedItem == NavItem.MAPEL1_ROMBEL) { onItemSelected(NavItem.MAPEL1_ROMBEL) }
                                        SidebarSubItemRow("Absensi Kelas", Icons.Default.Schedule, selectedItem == NavItem.MAPEL1_ABSENSI) { onItemSelected(NavItem.MAPEL1_ABSENSI) }
                                        SidebarSubItemRow("Penilaian Harian", Icons.Default.School, selectedItem == NavItem.MAPEL1) { onItemSelected(NavItem.MAPEL1) }
                                        SidebarSubItemRow("Penilaian UTS", Icons.AutoMirrored.Filled.Assignment, selectedItem == NavItem.MAPEL1_UTS) { onItemSelected(NavItem.MAPEL1_UTS) }
                                        SidebarSubItemRow("Penilaian PAS", Icons.AutoMirrored.Filled.Assignment, selectedItem == NavItem.MAPEL1_PAS) { onItemSelected(NavItem.MAPEL1_PAS) }
                                    }
                                }
                            }
                            
                            if (isMapel2Visible) {
                                SidebarNestedHeaderRow(mapel2Name, Icons.Default.Computer, infoExpanded) { infoExpanded = !infoExpanded }
                                AnimatedVisibility(visible = infoExpanded) {
                                    Column(modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 8.dp)) {
                                        SidebarSubItemRow("Kelola Rombel", Icons.Default.Group, selectedItem == NavItem.MAPEL2_ROMBEL) { onItemSelected(NavItem.MAPEL2_ROMBEL) }
                                        SidebarSubItemRow("Absensi Kelas", Icons.Default.Schedule, selectedItem == NavItem.MAPEL2_ABSENSI) { onItemSelected(NavItem.MAPEL2_ABSENSI) }
                                        SidebarSubItemRow("Penilaian Harian", Icons.Default.School, selectedItem == NavItem.MAPEL2) { onItemSelected(NavItem.MAPEL2) }
                                        SidebarSubItemRow("Penilaian UTS", Icons.AutoMirrored.Filled.Assignment, selectedItem == NavItem.MAPEL2_UTS) { onItemSelected(NavItem.MAPEL2_UTS) }
                                        SidebarSubItemRow("Penilaian PAS", Icons.AutoMirrored.Filled.Assignment, selectedItem == NavItem.MAPEL2_PAS) { onItemSelected(NavItem.MAPEL2_PAS) }
                                    }
                                }
                            }
                            
                            customSubjects.forEach { customSub ->
                                var customExpanded by remember(customSub.id) { mutableStateOf(false) }
                                SidebarNestedHeaderRow(customSub.namaMapel, Icons.Default.LibraryBooks, customExpanded) { customExpanded = !customExpanded }
                                AnimatedVisibility(visible = customExpanded) {
                                    Column(modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 8.dp)) {
                                        SidebarSubItemRow("Kelola Rombel", Icons.Default.Group, activeCustomNav?.subjectId == customSub.id && activeCustomNav?.subType == "ROMBEL") { viewModel.selectCustomSubjectNav(customSub.id, customSub.namaMapel, "ROMBEL") }
                                        SidebarSubItemRow("Absensi Kelas", Icons.Default.Schedule, activeCustomNav?.subjectId == customSub.id && activeCustomNav?.subType == "ABSENSI") { viewModel.selectCustomSubjectNav(customSub.id, customSub.namaMapel, "ABSENSI") }
                                        SidebarSubItemRow("Penilaian Harian", Icons.Default.School, activeCustomNav?.subjectId == customSub.id && activeCustomNav?.subType == "PENILAIAN_HARIAN") { viewModel.selectCustomSubjectNav(customSub.id, customSub.namaMapel, "PENILAIAN_HARIAN") }
                                        SidebarSubItemRow("Penilaian UTS", Icons.AutoMirrored.Filled.Assignment, activeCustomNav?.subjectId == customSub.id && activeCustomNav?.subType == "PENILAIAN_UTS") { viewModel.selectCustomSubjectNav(customSub.id, customSub.namaMapel, "PENILAIAN_UTS") }
                                        SidebarSubItemRow("Penilaian PAS", Icons.AutoMirrored.Filled.Assignment, activeCustomNav?.subjectId == customSub.id && activeCustomNav?.subType == "PENILAIAN_PAS") { viewModel.selectCustomSubjectNav(customSub.id, customSub.namaMapel, "PENILAIAN_PAS") }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            item {
                SidebarGroupHeaderPill("SETUP DAN REKAP", Icons.Default.SettingsSuggest, Color(0xFF90AD1D), setupExpanded) { setupExpanded = !setupExpanded }
            }
            item {
                AnimatedVisibility(visible = setupExpanded) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, top = 4.dp, bottom = 8.dp)
                    ) {
                        // Connecting line for the 4 features
                        Box(
                            modifier = Modifier
                                .padding(start = 12.dp, top = 10.dp, bottom = 10.dp)
                                .width(2.5.dp)
                                .height(165.dp)
                                .background(Color(0xFF90AD1D).copy(alpha = 0.6f))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            SidebarSubCardRow("Rekap Data (Excel)", "Export Backup & Laporan", Icons.Default.TableChart, Color(0xFF0F766E), selectedItem == NavItem.EXPORT) { onItemSelected(NavItem.EXPORT) }
                            SidebarSubCardRow("Atur Jadwal", "Kelola Jadwal Mengajar", Icons.Default.Schedule, Color(0xFFEAB308), selectedItem == NavItem.JADWAL) { onItemSelected(NavItem.JADWAL) }
                            SidebarSubCardRow("Fitur Tambahan", "Pengaturan Aplikasi", Icons.Default.GridOn, Color(0xFF90AD1D), selectedItem == NavItem.SETTINGS) { onItemSelected(NavItem.SETTINGS) }
                            SidebarSubCardRow("Database", "Manajemen Storage", Icons.Default.Storage, Color(0xFFEF4444), selectedItem == NavItem.DATABASE) { onItemSelected(NavItem.DATABASE) }
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val uriHandler = LocalUriHandler.current
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Untuk Update Aplikasi Lihat Pembaharuan Yang Tersedia Di :",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp
                                ),
                                textAlign = TextAlign.Center
                            )
                            Row(
                                modifier = Modifier.clickable {
                                    uriHandler.openUri("https://lynk.id/aplikasiqu")
                                },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                androidx.compose.foundation.Image(
                                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_g_icon),
                                    contentDescription = "App Icon",
                                    modifier = Modifier.size(16.dp).clip(RoundedCornerShape(4.dp))
                                )
                                Text(
                                    text = "Update Aplikasi",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF3B82F6),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        textDecoration = TextDecoration.Underline
                                    )
                                )
                            }
                            Text(
                                text = "Versi Saat ini : V01.01.08.26",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun SidebarMainPillButton(
    title: String,
    icon: ImageVector,
    bgColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 13.sp
            )
        )
    }
}

@Composable
fun SidebarGroupHeaderPill(
    title: String,
    icon: ImageVector,
    bgColor: Color,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable { onToggle() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 13.sp
            ),
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun SidebarNestedHeaderRow(
    title: String,
    icon: ImageVector,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF3B82F6),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 13.sp
            ),
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun SidebarSubItemRow(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val tintColor = if (isSelected) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    val bgColor = if (isSelected) Color(0xFF10B981).copy(alpha = 0.1f) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tintColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        )
    }
}

@Composable
fun SidebarSubCardRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) accentColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isSelected) accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(accentColor)
                    .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(15.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 11.5.sp
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 9.sp
                    )
                )
            }
        }
    }
}





@Composable
fun DesktopSidebar(
    selectedItem: NavItem,
    onItemSelected: (NavItem) -> Unit,
    viewModel: PesantrenViewModel,
    onProfileClick: () -> Unit,
    isProfileVerified: Boolean
) {
    Surface(
        modifier = Modifier
            .width(260.dp)
            .fillMaxHeight(),
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        SidebarContent(selectedItem, onItemSelected, viewModel, onProfileClick, isProfileVerified)
    }
}

@Composable
fun MobileDrawerOverlay(
    isOpen: Boolean,
    selectedItem: NavItem,
    onItemSelected: (NavItem) -> Unit,
    onCloseDrawer: () -> Unit,
    viewModel: PesantrenViewModel,
    onProfileClick: () -> Unit,
    isProfileVerified: Boolean
) {
    if (isOpen) {
        // Dummy to keep overlay logic working correctly within BoxWithConstraints or others if needed
        // Actually AnimatedVisibility handles whether it renders or not.
    }
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = isOpen,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                    .clickable { onCloseDrawer() }
            )
        }

        AnimatedVisibility(
            visible = isOpen,
            enter = slideInHorizontally(initialOffsetX = { -it }),
            exit = slideOutHorizontally(targetOffsetX = { -it })
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.65f) // Narrower
                    .shadow(elevation = 24.dp, shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp))
                    .clip(RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        width = 3.dp,
                        brush = Brush.horizontalGradient(listOf(Color.Transparent, Color(0xFF10B981))),
                        shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
                    )
            ) {
                SidebarContent(
                    selectedItem = selectedItem,
                    onItemSelected = {
                        onItemSelected(it)
                        onCloseDrawer()
                    },
                    viewModel = viewModel,
                    onProfileClick = onProfileClick,
                    isProfileVerified = isProfileVerified
                )
            }
        }
    }
}

@Composable
fun MobileHeader(
    currentTitle: String,
    onOpenDrawer: () -> Unit,
    onProfileClick: () -> Unit,
    isProfileVerified: Boolean,
    profilBase64: String = "",
    modifier: Modifier = Modifier,
    centerContent: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit = {}
) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    
    val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        shadowElevation = 8.dp,
        color = Color.Transparent,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF047857), Color(0xFF10B981))
                    )
                )
                .height(60.dp + topPadding) // accommodate status bar padding properly without squishing
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = topPadding)
            ) {
                Spacer(modifier = Modifier.height(6.dp))
                // Garis outline hijau tipis di atas
                HorizontalDivider(
                    color = Color(0xFF34D399).copy(alpha = 0.8f),
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                )
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Animated Hamburger (Left)
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable { onOpenDrawer() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Center Content (Notifications)
                    centerContent()

                    // Profile Icon (Right)
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clickable { onProfileClick() }
                    ) {
                    // Main 3D Icon Background
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center)
                            .shadow(6.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF34D399), Color(0xFF047857))
                                )
                            )
                            .border(1.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val cleanBase64 = if (profilBase64.contains(",")) profilBase64.substringAfter(",") else profilBase64
                        val imageBytes = if (cleanBase64.isNotBlank()) try { android.util.Base64.decode(cleanBase64, android.util.Base64.DEFAULT) } catch(e: Exception) { null } else null
                        val bitmap = if (imageBytes != null) try { android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size) } catch(e: Exception) { null } else null
                        if (bitmap != null) {
                            androidx.compose.foundation.Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Profile Picture",
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profil",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    
                    // Small Professional Badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 2.dp, y = (-2).dp)
                            .size(16.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(if (isProfileVerified) Color(0xFF10B981) else Color(0xFFEF4444))
                            .border(1.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isProfileVerified) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = if (isProfileVerified) "Verified" else "Unverified",
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }
        }
    }
}
}

@Composable
fun ProfileDialog(
    viewModel: com.example.ui.viewmodel.PesantrenViewModel,
    onDismiss: () -> Unit
) {
    val namaGuru by viewModel.namaGuru.collectAsState()
    val namaSekolah by viewModel.namaSekolah.collectAsState()
    val alamatSekolah by viewModel.alamatSekolah.collectAsState()
    val jenisKelamin by viewModel.jenisKelamin.collectAsState()
    val profilBase64 by viewModel.profilBase64.collectAsState()
    
    var editNamaGuru by remember { mutableStateOf(namaGuru) }
    var editNamaSekolah by remember { mutableStateOf(namaSekolah) }
    var editAlamat by remember { mutableStateOf(alamatSekolah) }
    var editJK by remember { mutableStateOf(jenisKelamin) }
    var editBase64 by remember { mutableStateOf(profilBase64) }
    
    var showSizeError by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val bytes = inputStream.readBytes()
                    if (bytes.size > 1024 * 1024) { // 1 MB
                        showSizeError = true
                    } else {
                        showSizeError = false
                        // Scale and encode
                        val originalBitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        val scaledBitmap = android.graphics.Bitmap.createScaledBitmap(originalBitmap, 330, 330, true)
                        val outputStream = java.io.ByteArrayOutputStream()
                        scaledBitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, outputStream)
                        val scaledBytes = outputStream.toByteArray()
                        editBase64 = android.util.Base64.encodeToString(scaledBytes, android.util.Base64.DEFAULT)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        try {
            imagePicker.launch("image/*")
        } catch (e: Exception) {
            Toast.makeText(context, "Pilih foto dari galeri", Toast.LENGTH_SHORT).show()
        }
    }

    val requestGalleryPermission = {
        val perm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        val isGranted = androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            perm
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (isGranted || Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                imagePicker.launch("image/*")
            } catch (e: Exception) {
                permissionLauncher.launch(perm)
            }
        } else {
            permissionLauncher.launch(perm)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.Transparent,
            shadowElevation = 24.dp,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF047857), Color(0xFF064E3B))
                        )
                    )
                    .padding(4.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Profil Pengguna",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                        
                        // Image Upload
                        Box(
                            modifier = Modifier
                                .size(130.dp)
                                .padding(bottom = 10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.outline)
                                    .border(2.dp, Color(0xFF10B981), CircleShape)
                                    .clickable { requestGalleryPermission() }
                                    .align(Alignment.TopCenter),
                                contentAlignment = Alignment.Center
                            ) {
                                if (editBase64.isNotEmpty()) {
                                    val imageBytes = try { android.util.Base64.decode(editBase64, android.util.Base64.DEFAULT) } catch(e: Exception) { null }
                                    val bitmap = if (imageBytes != null) try { android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size) } catch(e: Exception) { null } else null
                                    if (bitmap != null) {
                                        androidx.compose.foundation.Image(
                                            bitmap = bitmap.asImageBitmap(),
                                            contentDescription = "Profile Picture",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                        )
                                    } else {
                                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                                    }
                                } else {
                                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                                }
                            }
                            
                            // Small Add Icon at the bottom center
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .size(32.dp)
                                    .offset(y = (-6).dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                                    .border(2.dp, Color.White, CircleShape)
                                    .clickable { requestGalleryPermission() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Tambah Foto", modifier = Modifier.size(20.dp), tint = Color.White)
                            }
                        }
                        
                        Text(
                            text = "Maksimal 1 MB dan ukuran 330px x 330px",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)),
                            textAlign = TextAlign.Center
                        )
                        
                        if (showSizeError) {
                            Text(
                                text = "Foto melebihi 1 MB!",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.Red, fontWeight = FontWeight.Bold),
                                textAlign = TextAlign.Center
                            )
                        }

                        var isSekolahExpanded by remember { mutableStateOf(false) }
                        var isGuruExpanded by remember { mutableStateOf(false) }

                        // Dropdown Sekolah
                        Surface(
color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp),
                            shadowElevation = 4.dp,
                            modifier = Modifier.fillMaxWidth().clickable { isSekolahExpanded = !isSekolahExpanded }
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Data Sekolah", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = Color(0xFF047857)))
                                    Icon(if (isSekolahExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, null, tint = Color(0xFF047857))
                                }
                                AnimatedVisibility(visible = isSekolahExpanded) {
                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        OutlinedTextField(value = editNamaSekolah, onValueChange = { newValue -> editNamaSekolah = newValue }, label = { Text("Nama Sekolah") }, modifier = Modifier.fillMaxWidth())
                                        OutlinedTextField(value = editAlamat, onValueChange = { newValue -> editAlamat = newValue }, label = { Text("Alamat Sekolah") }, modifier = Modifier.fillMaxWidth())
                                    }
                                }
                            }
                        }

                        // Dropdown Guru
                        Surface(
color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp),
                            shadowElevation = 4.dp,
                            modifier = Modifier.fillMaxWidth().clickable { isGuruExpanded = !isGuruExpanded }
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Data Guru", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = Color(0xFF047857)))
                                    Icon(if (isGuruExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, null, tint = Color(0xFF047857))
                                }
                                AnimatedVisibility(visible = isGuruExpanded) {
                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        OutlinedTextField(value = editNamaGuru, onValueChange = { newValue -> editNamaGuru = newValue }, label = { Text("Nama Guru") }, modifier = Modifier.fillMaxWidth())
                                        
                                        var jkExpanded by remember { mutableStateOf(false) }
                                        Box {
                                            OutlinedTextField(
                                                value = editJK, onValueChange = { }, readOnly = true, label = { Text("Jenis Kelamin") }, modifier = Modifier.fillMaxWidth(),
                                                trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.clickable { jkExpanded = true }) }
                                            )
                                            Box(modifier = Modifier.matchParentSize().background(Color.Transparent).clickable { jkExpanded = true })
                                            DropdownMenu(expanded = jkExpanded, onDismissRequest = { jkExpanded = false }) {
                                                DropdownMenuItem(text = { Text("Laki-laki") }, onClick = { editJK = "Laki-laki"; jkExpanded = false })
                                                DropdownMenuItem(text = { Text("Perempuan") }, onClick = { editJK = "Perempuan"; jkExpanded = false })
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Button(
                            onClick = {
                                viewModel.updateProfile(editNamaGuru, editNamaSekolah, editAlamat, editJK, editBase64)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Text("Simpan Perubahan", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)
                        }
                        
                        TextButton(onClick = onDismiss) {
                            Text("Batal", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
