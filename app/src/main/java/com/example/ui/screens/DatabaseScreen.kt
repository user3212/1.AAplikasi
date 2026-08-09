package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import java.io.OutputStreamWriter
import java.io.BufferedReader
import java.io.InputStreamReader

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.PesantrenViewModel
import android.content.Context

@Composable
fun DatabaseScreen(viewModel: PesantrenViewModel, modifier: Modifier = Modifier) {
    val importResultLog by viewModel.importResultLog.collectAsState()
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    
    val activeSemester = sharedPrefs.getString("active_semester", "Semester Ganjil") ?: "Semester Ganjil"
    val activeAcademicYear = sharedPrefs.getString("active_academic_year", "2026/2027") ?: "2026/2027"

    var showFirstConfirmDialog by remember { mutableStateOf(false) }
    var showSecondConfirmDialog by remember { mutableStateOf(false) }
    
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri: android.net.Uri? ->
        if (uri != null) {
            viewModel.showLoadingOverlay("Mengekspor database...")
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        java.io.OutputStreamWriter(outputStream).use { writer ->
                            val jsonString = viewModel.exportBackupJson()
                            writer.write(jsonString)
                        }
                    }
            } catch (e: Exception) {
                viewModel.showToast("Gagal mengekspor database")
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: android.net.Uri? ->
        if (uri != null) {
            viewModel.showLoadingOverlay("Mengimpor database...")
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val reader = java.io.BufferedReader(java.io.InputStreamReader(inputStream))
                    val jsonString = reader.readText()
                    viewModel.importBackupJson(jsonString)
                }
            } catch (e: Exception) {
                viewModel.showToast("Gagal membaca file backup")
            } finally {
            }
        }
    }


    
    if (importResultLog != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearImportResultLog() },
            title = {
                Text("Hasil Impor Database", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(text = importResultLog!!, fontSize = 14.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.clearImportResultLog() }) {
                    Text("Tutup", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showFirstConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showFirstConfirmDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Konfirmasi Hapus (1/2)")
                }
            },
            text = { Text("Apakah Anda yakin ingin menghapus database aktif ($activeSemester - $activeAcademicYear)?") },
            confirmButton = {
                TextButton(onClick = {
                    showFirstConfirmDialog = false
                    showSecondConfirmDialog = true
                }) {
                    Text("Lanjutkan", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFirstConfirmDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    if (showSecondConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showSecondConfirmDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Konfirmasi Final (2/2)")
                }
            },
            text = { Text("Tindakan ini tidak dapat dibatalkan. Semua data pada $activeSemester - $activeAcademicYear akan hilang selamanya. Yakin?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearDatabase()
                    viewModel.showToast("Database aktif telah dihapus")
                    showSecondConfirmDialog = false
                }) {
                    Text("Ya, Hapus Permanen", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSecondConfirmDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Manajemen Database",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )

        Surface(
color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Database Aktif: $activeSemester ($activeAcademicYear)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Button(
                    onClick = { showFirstConfirmDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Hapus Database Aktif")
                }
            }
        }

        Surface(
color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Backup & Restore",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Button(
                    onClick = { exportLauncher.launch("backup_database_pesantrenqu.json") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Backup & Export Database")
                }

                Button(
                    onClick = { importLauncher.launch(arrayOf("application/json", "*/*")) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Import & Restore Database")
                }
            }
        }
    }
}
