package com.example.ui.screens

import android.Manifest
import android.app.TimePickerDialog
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CustomDropdown
import com.example.ui.components.CustomInputField
import com.example.ui.util.SoundHelper
import com.example.ui.viewmodel.HariJadwal
import com.example.ui.viewmodel.JamMengajar
import com.example.ui.viewmodel.PesantrenViewModel
import java.util.Calendar

import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import java.util.Locale
import java.text.SimpleDateFormat
import com.example.ui.util.NotificationHelper

@Composable
fun JadwalScreen(viewModel: PesantrenViewModel) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.showToast("Izin notifikasi diberikan. Jadwal disimpan.")
        } else {
            viewModel.showToast("Izin notifikasi ditolak. Notifikasi mungkin tidak muncul.")
        }
    }

    val requestNotificationPermission = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            viewModel.showToast("Jadwal berhasil disimpan.")
        }
    }

    val jadwalList by viewModel.jadwalList.collectAsState()
    val jadwalSound by viewModel.jadwalSound.collectAsState()
    val jadwalRepetition by viewModel.jadwalRepetition.collectAsState()

    var showConfigExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEAB308))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Atur Jadwal Mengajar", style = MaterialTheme.typography.titleLarge.copy(color = Color.White, fontWeight = FontWeight.Bold))
                        Text("Pengingat otomatis 10 menit sebelum masuk", style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.9f)))
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daftar Jadwal",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                )
                Button(
                    onClick = {
                        viewModel.saveJadwal(jadwalList.toMutableList().apply {
                            add(HariJadwal(java.util.UUID.randomUUID().toString(), "Senin", true, mutableListOf(JamMengajar(java.util.UUID.randomUUID().toString(), "Jam ke-1", "08:00", "09:00"))))
                        })
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981), contentColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tambah", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Section: Jadwal List
        items(jadwalList.toList()) { jadwal ->
            val dayOptions = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF3B82F6))
                    .padding(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val index = jadwalList.indexOf(jadwal)
                                if (index != -1) {
                                    val newList = jadwalList.toMutableList()
                                    newList[index] = jadwal.copy(isExpanded = !jadwal.isExpanded)
                                    viewModel.saveJadwal(newList)
                                }
                            }
                            .background(Color(0xFFEFF6FF))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (jadwal.hari.isEmpty()) "Hari Belum Diatur" else "Hari ${jadwal.hari}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
                            )
                            if (!jadwal.isExpanded && jadwal.jamList.isNotEmpty()) {
                                Text(
                                    text = "${jadwal.jamList.size} Sesi Jadwal",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF3B82F6))
                                )
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                viewModel.saveJadwal(jadwalList.filter { it.id != jadwal.id })
                            }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Hapus Jadwal", tint = Color.Red)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(if (jadwal.isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color(0xFF1E3A8A))
                        }
                    }

                    if (jadwal.isExpanded) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            CustomDropdown(
                                label = "PILIH HARI MENGAJAR",
                                options = dayOptions,
                                selectedOption = if (jadwal.hari.isEmpty()) "Senin" else jadwal.hari,
                                onOptionSelected = { selectedDay ->
                                    val index = jadwalList.indexOfFirst { it.id == jadwal.id }
                                    if (index != -1 && selectedDay != null) {
                                        val newList = jadwalList.toMutableList()
                                        newList[index] = newList[index].copy(hari = selectedDay)
                                        viewModel.saveJadwal(newList)
                                    }
                                },
                                optionToString = { it },
                                focusAccentColor = Color(0xFF3B82F6)
                            )
                            
                            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), thickness = 1.dp)
                            
                            jadwal.jamList.forEachIndexed { jamIndex, jam ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        TimePickerField(
                                            value = jam.waktuMulai,
                                            onValueChange = {
                                                val index = jadwalList.indexOfFirst { it.id == jadwal.id }
                                                if (index != -1) {
                                                    val newList = jadwalList.toMutableList()
                                                    val newJamList = jadwal.jamList.toMutableList()
                                                    newJamList[jamIndex] = jam.copy(waktuMulai = it)
                                                    newList[index] = jadwal.copy(jamList = newJamList)
                                                    viewModel.saveJadwal(newList)
                                                }
                                            },
                                            label = "Mulai",
                                            placeholder = "08:00",
                                            focusAccentColor = Color(0xFF3B82F6)
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        TimePickerField(
                                            value = jam.waktuSelesai,
                                            onValueChange = {
                                                val index = jadwalList.indexOfFirst { it.id == jadwal.id }
                                                if (index != -1) {
                                                    val newList = jadwalList.toMutableList()
                                                    val newJamList = jadwal.jamList.toMutableList()
                                                    newJamList[jamIndex] = jam.copy(waktuSelesai = it)
                                                    newList[index] = jadwal.copy(jamList = newJamList)
                                                    viewModel.saveJadwal(newList)
                                                }
                                            },
                                            label = "Selesai",
                                            placeholder = "09:30",
                                            focusAccentColor = Color(0xFF3B82F6)
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            val index = jadwalList.indexOfFirst { it.id == jadwal.id }
                                            if (index != -1) {
                                                val newList = jadwalList.toMutableList()
                                                val newJamList = jadwal.jamList.toMutableList()
                                                newJamList.removeAt(jamIndex)
                                                newList[index] = jadwal.copy(jamList = newJamList)
                                                viewModel.saveJadwal(newList)
                                            }
                                        },
                                        modifier = Modifier.padding(top = 16.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Hapus Jam", tint = Color.Red.copy(alpha = 0.7f))
                                    }
                                }
                            }
                            Button(
                                onClick = {
                                    val index = jadwalList.indexOfFirst { it.id == jadwal.id }
                                    if (index != -1) {
                                        val newList = jadwalList.toMutableList()
                                        val newJamList = jadwal.jamList.toMutableList()
                                        newJamList.add(JamMengajar(java.util.UUID.randomUUID().toString(), "Jam", "00:00", "00:00"))
                                        newList[index] = jadwal.copy(jamList = newJamList)
                                        viewModel.saveJadwal(newList)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFF6FF), contentColor = Color(0xFF1E3A8A)),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Tambah Jam Mengajar", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Section: Notification Config (Konfigurasi Tahfiz) moved to the bottom
        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showConfigExpanded = !showConfigExpanded }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Konfigurasi Notifikasi",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        )
                        Icon(if (showConfigExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null)
                    }

                    AnimatedVisibility(visible = showConfigExpanded) {
                        Column(
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            var isPlayingSound by remember { mutableStateOf(false) }
                            var showSoundExpanded by remember { mutableStateOf(false) }
                            
                            // Sound config
                            Box {
                                OutlinedTextField(
                                    value = jadwalSound,
                                    onValueChange = { },
                                    readOnly = true,
                                    label = { Text("Nada Notifikasi") },
                                    modifier = Modifier.fillMaxWidth(),
                                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.clickable { showSoundExpanded = true }) }
                                )
                                Box(modifier = Modifier.matchParentSize().background(Color.Transparent).clickable { showSoundExpanded = true })
                                DropdownMenu(expanded = showSoundExpanded, onDismissRequest = { showSoundExpanded = false }) {
                                    listOf("Nada 1", "Nada 2", "Nada 3").forEach { sound ->
                                        DropdownMenuItem(
                                            text = { Text(sound) },
                                            onClick = {
                                                viewModel.updateJadwalConfig(sound, jadwalRepetition)
                                                showSoundExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                            
                            // Button Uji Suara / Play Sound
                            Button(
                                onClick = {
                                    if (isPlayingSound) {
                                        SoundHelper.stopSound()
                                        isPlayingSound = false
                                    } else {
                                        isPlayingSound = true
                                        viewModel.showToast("Memutar $jadwalSound...")
                                        SoundHelper.playSound(context, jadwalSound, jadwalRepetition) {
                                            isPlayingSound = false
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isPlayingSound) Color(0xFFEF4444) else Color(0xFF10B981)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = if (isPlayingSound) Icons.Default.Stop else Icons.Default.VolumeUp,
                                    contentDescription = "Uji Suara",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isPlayingSound) "Hentikan Suara" else "Play Sound (Uji $jadwalSound)",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = { requestNotificationPermission() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground, contentColor = Color.White),
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 24.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Simpan Jadwal", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun TimePickerField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    focusAccentColor: Color = MaterialTheme.colorScheme.primary
) {
    val context = LocalContext.current
    Box(modifier = modifier.clickable {
        val calendar = Calendar.getInstance()
        if (value.isNotEmpty() && value.contains(":")) {
            val parts = value.split(":")
            calendar.set(Calendar.HOUR_OF_DAY, parts[0].toIntOrNull() ?: 0)
            calendar.set(Calendar.MINUTE, parts[1].toIntOrNull() ?: 0)
        }
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                onValueChange(String.format("%02d:%02d", hourOfDay, minute))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            android.text.format.DateFormat.is24HourFormat(context)
        ).show()
    }) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            label = { Text(label, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)) },
            placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), fontSize = 13.sp) },
            readOnly = true,
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledContainerColor = MaterialTheme.colorScheme.surface,
                disabledBorderColor = Color(0xFFCBD5E1),
                disabledTextColor = MaterialTheme.colorScheme.onBackground,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = { Icon(Icons.Default.Schedule, contentDescription = "Pilih Waktu", tint = focusAccentColor) }
        )
    }
}
