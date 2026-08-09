import re

with open("app/src/main/java/com/example/ui/screens/TahfizScreen.kt", "r") as f:
    content = f.read()

# Replace SetoranHafalanView
start_idx = content.find("@Composable\nfun SetoranHafalanView")
if start_idx != -1:
    end_idx = content.find("@Composable\nfun EvaluasiKualitasView")
    if end_idx != -1:
        new_content = """@Composable
fun SetoranHafalanView(viewModel: PesantrenViewModel, modifier: Modifier = Modifier) {
    val santriList by viewModel.santriList.collectAsState()
    val tahfizRecords by viewModel.tahfizRecords.collectAsState()
    
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    var tanggal by remember { mutableStateOf(dateFormatter.format(Date())) }
    val context = LocalContext.current
    
    var selectedSantri by remember { mutableStateOf<Santri?>(null) }
    var santriJuz by remember(selectedSantri) { mutableStateOf(if (selectedSantri != null) viewModel.getSantriJuz(selectedSantri!!.id) else viewModel.getConfigJuz()) }
    
    val surahJuzData = remember(santriJuz) {
        when (santriJuz) {
            "Juz 1" -> listOf("Al-Fatihah" to 7, "Al-Baqarah" to 141)
            "Juz 29" -> listOf(
                "Al-Mulk" to 30, "Al-Qalam" to 52, "Al-Haqqah" to 52, "Al-Ma'arij" to 44, "Nuh" to 28,
                "Al-Jinn" to 28, "Al-Muzzammil" to 20, "Al-Muddaththir" to 56, "Al-Qiyamah" to 40,
                "Al-Insan" to 31, "Al-Mursalat" to 50
            )
            else -> listOf( // Default to Juz 30
                "An-Naba'" to 40, "An-Nazi'at" to 46, "'Abasa" to 42, "At-Takwir" to 29, "Al-Infitar" to 19, "Al-Mutaffifin" to 36,
                "Al-Inshiqaq" to 25, "Al-Buruj" to 22, "At-Tariq" to 17, "Al-A'la" to 19, "Al-Ghashiyah" to 26, "Al-Fajr" to 30,
                "Al-Balad" to 20, "Ash-Shams" to 15, "Al-Layl" to 21, "Ad-Duha" to 11, "Ash-Sharh" to 8, "At-Tin" to 8, "Al-'Alaq" to 19,
                "Al-Qadr" to 5, "Al-Bayyinah" to 8, "Az-Zalzalah" to 8, "Al-'Adiyat" to 11, "Al-Qari'ah" to 11, "At-Takathur" to 8,
                "Al-'Asr" to 3, "Al-Humazah" to 9, "Al-Fil" to 5, "Quraysh" to 4, "Al-Ma'un" to 7, "Al-Kawthar" to 3, "Al-Kafirun" to 6,
                "An-Nasr" to 3, "Al-Masad" to 5, "Al-Ikhlas" to 4, "Al-Falaq" to 5, "An-Nas" to 6
            )
        }
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
        AlertDialog(
            onDismissRequest = { showJuzCompletedPopup = false },
            title = { Text("Juz Selesai!", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Alhamdulillah, santri telah menyelesaikan $santriJuz. Silakan pilih juz selanjutnya untuk santri ini:", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    val nextOptions = listOf("Juz 1", "Juz 29", "Juz 30").filter { it != santriJuz }
                    nextOptions.forEach { option ->
                        TextButton(onClick = { 
                            if (selectedSantri != null) {
                                viewModel.setSantriJuz(selectedSantri!!.id, option)
                                santriJuz = option
                            }
                            showJuzCompletedPopup = false
                        }) {
                            Text(option, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {
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
                        .clickable {
                            val cal = java.util.Calendar.getInstance()
                            android.app.DatePickerDialog(
                                context,
                                { _, y, m, d ->
                                    val selectedDate = java.util.Calendar.getInstance()
                                    selectedDate.set(y, m, d)
                                    tanggal = dateFormatter.format(selectedDate.time)
                                },
                                cal.get(java.util.Calendar.YEAR),
                                cal.get(java.util.Calendar.MONTH),
                                cal.get(java.util.Calendar.DAY_OF_MONTH)
                            ).show()
                        }
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
                                santriJuz = viewModel.getSantriJuz(it.id)
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
                            Text("SURAH (${santriJuz.uppercase()})", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF4B5563)))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        CustomDropdown(
                            label = "",
                            options = surahJuzData,
                            selectedOption = selectedSurahPair,
                            onOptionSelected = { selectedSurahPair = it; tambahanAyat = "0" },
                            optionToString = { it.first },
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
                                    android.widget.Toast.makeText(context, "Masukkan tambahan ayat yang valid.", android.widget.Toast.LENGTH_SHORT).show()
                                } else if (tambah > sisa) {
                                    android.widget.Toast.makeText(context, "Ayat berlebih! Sisa ayat di surah ini adalah $sisa", android.widget.Toast.LENGTH_LONG).show()
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
                                    android.widget.Toast.makeText(context, "Setoran berhasil disimpan", android.widget.Toast.LENGTH_SHORT).show()
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
                                android.widget.Toast.makeText(context, "Pilih santri dan surah.", android.widget.Toast.LENGTH_SHORT).show()
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
                                    Text(selectedSantri!!.nama, color = Color(0xFF6EE7B7), fontWeight = FontWeight.Bold)
                                    Text(lastRecord.surahJuz, color = Color.White, fontWeight = FontWeight.Bold)
                                    Text(lastRecord.ayatSelesai, color = Color(0xFF6EE7B7), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
"""
        with open("app/src/main/java/com/example/ui/screens/TahfizScreen.kt", "w") as f:
            f.write(content[:start_idx] + new_content + content[end_idx:])
        print("Success")
    else:
        print("End function not found")
else:
    print("Start function not found")
