package com.example

import android.os.Bundle
import com.example.ui.components.ProfileDialog
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.border

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DesktopSidebar
import com.example.ui.components.MobileDrawerOverlay
import com.example.ui.components.MobileHeader
import com.example.ui.components.NavItem
import com.example.ui.components.ToastNotification

import com.example.ui.components.AlarmPopupNotification

import com.example.ui.screens.AttendanceScreen
import com.example.ui.screens.BerandaScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExportBackupScreen
import com.example.ui.screens.GenericMapelScreen
import com.example.ui.screens.Mapel1Screen
import com.example.ui.screens.Mapel2Screen
import com.example.ui.screens.SantriScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.DatabaseScreen
import com.example.ui.screens.TahfizScreen
import com.example.ui.screens.TahfizKonfigurasiScreen
import androidx.activity.SystemBarStyle
import android.graphics.Color as AndroidColor
import com.example.ui.screens.JadwalScreen
import com.example.ui.theme.BgPrimary
import com.example.ui.theme.PesantrenquTheme
import com.example.ui.viewmodel.PesantrenViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: PesantrenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                AndroidColor.TRANSPARENT
            )
        )

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            PesantrenquTheme(isDarkMode = isDarkMode) {
                PesantrenquApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun PesantrenquApp(viewModel: PesantrenViewModel) {
    val selectedNavItem by viewModel.selectedNavItem.collectAsState()
    val isDrawerOpen by viewModel.isDrawerOpen.collectAsState()
    
    val toastMessage by viewModel.toastMessage.collectAsState()
    val alarmMessage by viewModel.alarmPopup.collectAsState()
    val isLoadingOverlayVisible by viewModel.isLoadingOverlayVisible.collectAsState()
    val loadingOverlayMessage by viewModel.loadingOverlayMessage.collectAsState()

    val activeCustomNav by viewModel.activeCustomSubjectNav.collectAsState()
    val profilBase64 by viewModel.profilBase64.collectAsState()

    
    val namaGuru by viewModel.namaGuru.collectAsState()
    val isProfileVerified = namaGuru.isNotBlank()
    var showProfileDialog by remember { mutableStateOf(false) }

    LaunchedEffect(isProfileVerified) {
        if (!isProfileVerified) {
            showProfileDialog = true
        }
    }

    if (showProfileDialog) {
        com.example.ui.components.ProfileDialog(viewModel = viewModel, onDismiss = { showProfileDialog = false })
    }

    val headerTitle = if (activeCustomNav != null) {
        "${activeCustomNav?.subjectName} (${activeCustomNav?.subType})"
    } else {
        selectedNavItem.title
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isWideScreen = maxWidth >= 600.dp

            if (isWideScreen) {
                // DESKTOP / TABLET LAYOUT: 260dp Sidebar Left + Main Content Right
                Row(modifier = Modifier.fillMaxSize()) {
                    DesktopSidebar(
                        selectedItem = selectedNavItem,
                        onItemSelected = { viewModel.selectNavItem(it) },
                        viewModel = viewModel,
                        onProfileClick = { showProfileDialog = true },
                        isProfileVerified = isProfileVerified
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        MainContentArea(
                            selectedNavItem = selectedNavItem,
                            viewModel = viewModel
                        )
                        
                        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 56.dp, vertical = 8.dp), contentAlignment = Alignment.TopCenter) {
                            ToastNotification(
                                message = toastMessage ?: "",
                                isVisible = toastMessage != null,
                                onDismiss = { viewModel.clearToast() },
                                accentColor = selectedNavItem.accentColor
                            )
                            AlarmPopupNotification(
                                message = alarmMessage ?: "",
                                isVisible = alarmMessage != null,
                                onDismiss = { viewModel.clearAlarm() }
                            )
                        }
                    }
                }
            } else {
                // MOBILE LAYOUT: Mobile Header + Drawer Overlay + Main Content
                Column(modifier = Modifier.fillMaxSize()) {
                    MobileHeader(
                        currentTitle = headerTitle,
                        onOpenDrawer = { viewModel.toggleDrawer(!isDrawerOpen) },
                        onProfileClick = { showProfileDialog = true },
                        isProfileVerified = isProfileVerified,
                        profilBase64 = profilBase64
                    ) {
                        Box(modifier = Modifier.weight(1f).padding(horizontal = 8.dp), contentAlignment = Alignment.Center) {
                            ToastNotification(
                                message = toastMessage ?: "",
                                isVisible = toastMessage != null,
                                onDismiss = { viewModel.clearToast() },
                                accentColor = selectedNavItem.accentColor,
                                modifier = Modifier.fillMaxWidth()
                            )
                            AlarmPopupNotification(
                                message = alarmMessage ?: "",
                                isVisible = alarmMessage != null,
                                onDismiss = { viewModel.clearAlarm() },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        MainContentArea(
                            selectedNavItem = selectedNavItem,
                            viewModel = viewModel
                        )

                        // Section F & V: Mobile Navigation Drawer (75% width overlay)
                        MobileDrawerOverlay(
                            isOpen = isDrawerOpen,
                            selectedItem = selectedNavItem,
                            onItemSelected = { viewModel.selectNavItem(it) },
                            onCloseDrawer = { viewModel.toggleDrawer(false) },
                            viewModel = viewModel,
                            onProfileClick = { showProfileDialog = true },
                            isProfileVerified = isProfileVerified
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainContentArea(
    selectedNavItem: NavItem,
    viewModel: PesantrenViewModel
) {
    val activeCustomNav by viewModel.activeCustomSubjectNav.collectAsState()
    val profilBase64 by viewModel.profilBase64.collectAsState()


    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            if (activeCustomNav != null) {
                GenericMapelScreen(viewModel = viewModel, customNav = activeCustomNav!!)
            } else {
                Crossfade(targetState = selectedNavItem, label = "ScreenTransition") { screen ->
                    when (screen) {
                        NavItem.BERANDA -> BerandaScreen(viewModel = viewModel)
                        NavItem.DASHBOARD -> DashboardScreen(viewModel = viewModel, onNavigate = { viewModel.selectNavItem(it) })
                        NavItem.SANTRI -> SantriScreen(viewModel = viewModel)
                        NavItem.ABSENSI -> AttendanceScreen(viewModel = viewModel)
                        NavItem.TAHFIZ, NavItem.EVALUASI_TAHFIZ, NavItem.REKAP_TAHFIZ -> TahfizScreen(viewModel = viewModel, navItem = screen)
                        NavItem.KONFIG_TAHFIZ -> TahfizKonfigurasiScreen(viewModel = viewModel)
                        NavItem.MAPEL1_ROMBEL, NavItem.MAPEL1_ABSENSI, NavItem.MAPEL1, NavItem.MAPEL1_UTS, NavItem.MAPEL1_PAS -> Mapel1Screen(viewModel = viewModel, navItem = screen)
                        NavItem.MAPEL2_ROMBEL, NavItem.MAPEL2_ABSENSI, NavItem.MAPEL2, NavItem.MAPEL2_UTS, NavItem.MAPEL2_PAS -> Mapel2Screen(viewModel = viewModel, navItem = screen)
                        NavItem.EXPORT -> ExportBackupScreen(viewModel = viewModel)
                        NavItem.SETTINGS -> SettingsScreen(viewModel = viewModel)
                        NavItem.JADWAL -> JadwalScreen(viewModel = viewModel)
                        NavItem.DATABASE -> DatabaseScreen(viewModel = viewModel)
                    }
                }
            }
        }
        
        // Footer (Aplikasi Guruqu...)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(androidx.compose.ui.graphics.Color(0xFF10B981), androidx.compose.ui.graphics.Color(0xFF047857))
                    )
                )
                .border(
                    width = 1.dp,
                    color = androidx.compose.ui.graphics.Color(0xFF34D399)
                )
                .navigationBarsPadding()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            androidx.compose.material3.Text(
                text = "Aplikasi Guruqu Dari Guru Untuk Guru Hebat",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = androidx.compose.ui.graphics.Color.White,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                    fontSize = 11.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            )
        }
    }
}
