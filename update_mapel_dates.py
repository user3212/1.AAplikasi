import re

for filename in ["app/src/main/java/com/example/ui/screens/Mapel1Screen.kt", "app/src/main/java/com/example/ui/screens/Mapel2Screen.kt"]:
    with open(filename, "r") as f:
        content = f.read()
    
    # Replace the first AlertDialog (Attendance)
    old_dialog_1 = """    if (showDatePickerDialog) {
        var tempDateInput by remember { mutableStateOf(formatDbToDisplayDate(currentDate)) }
        AlertDialog(
            onDismissRequest = { showDatePickerDialog = false },
            title = {
                Text("Pilih Tanggal Presensi", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Masukkan tanggal dengan format DD/MM/YYYY:", style = MaterialTheme.typography.bodyMedium)
                    OutlinedTextField(
                        value = tempDateInput,
                        onValueChange = { tempDateInput = it },
                        singleLine = true,
                        placeholder = { Text("DD/MM/YYYY") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val todayFormatted = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        AssistChip(
                            onClick = {
                                currentDate = todayFormatted
                                showDatePickerDialog = false
                            },
                            label = { Text("Hari Ini") }
                        )
                        AssistChip(
                            onClick = {
                                val cal = java.util.Calendar.getInstance()
                                cal.add(java.util.Calendar.DAY_OF_MONTH, -1)
                                currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
                                showDatePickerDialog = false
                            },
                            label = { Text("Kemarin") }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        try {
                            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            val parsedDate = sdf.parse(tempDateInput)
                            if (parsedDate != null) {
                                currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(parsedDate)
                            }
                        } catch (_: Exception) {}
                        showDatePickerDialog = false
                    }
                ) {
                    Text("Terapkan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }"""
    
    new_dialog_1 = """    if (showDatePickerDialog) {
        com.example.ui.components.ModernDatePickerDialog(
            initialDateMillis = null,
            onDateSelected = { millis ->
                currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(java.util.Date(millis))
            },
            onDismiss = { showDatePickerDialog = false }
        )
    }"""
    
    content = content.replace(old_dialog_1, new_dialog_1)
    
    # Replace the second AlertDialog (Grades)
    old_dialog_2 = """    if (showDatePickerDialog) {
        AlertDialog(
            onDismissRequest = { showDatePickerDialog = false },
            title = { Text("Pilih Tanggal Penilaian", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Masukkan tanggal dengan format DD/MM/YYYY:", style = MaterialTheme.typography.bodyMedium)
                    OutlinedTextField(
                        value = tempDateInput,
                        onValueChange = { tempDateInput = it },
                        singleLine = true,
                        placeholder = { Text("DD/MM/YYYY") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val todayFormatted = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        AssistChip(
                            onClick = {
                                currentDate = todayFormatted
                                showDatePickerDialog = false
                            },
                            label = { Text("Hari Ini") }
                        )
                        AssistChip(
                            onClick = {
                                val cal = java.util.Calendar.getInstance()
                                cal.add(java.util.Calendar.DAY_OF_MONTH, -1)
                                currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
                                showDatePickerDialog = false
                            },
                            label = { Text("Kemarin") }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        try {
                            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            val parsedDate = sdf.parse(tempDateInput)
                            if (parsedDate != null) {
                                currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(parsedDate)
                            }
                        } catch (_: Exception) {}
                        showDatePickerDialog = false
                    }
                ) {
                    Text("Terapkan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }"""
    
    content = content.replace(old_dialog_2, new_dialog_1)
    
    with open(filename, "w") as f:
        f.write(content)

