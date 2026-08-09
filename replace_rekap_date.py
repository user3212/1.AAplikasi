import re

with open("app/src/main/java/com/example/ui/screens/TahfizScreen.kt", "r") as f:
    content = f.read()

old_dialog = """    if (showDatePicker) {
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            title = { Text("Pilih Tanggal Penarikan Data", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Pilih tanggal untuk asset PNG/PDF:", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    CustomInputField(
                        value = selectedDate,
                        onValueChange = { selectedDate = it },
                        label = "Tanggal (dd/MM/yyyy)",
                        placeholder = "e.g., 20/07/2024",
                        leadingIcon = Icons.Default.CalendarToday
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { 
                    showDatePicker = false
                    if (exportPendingType == "PNG") {
                        exportPngLauncher.launch("rekap_tahfiz_${selectedRombel ?: "semua"}.png")
                    } else if (exportPendingType == "PDF") {
                        exportPdfLauncher.launch("rekap_tahfiz_${selectedRombel ?: "semua"}.pdf")
                    }
                    exportPendingType = null
                }) {
                    Text("Pilih", color = Color(0xFF047857), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                }
            }
        )
    }"""

new_dialog = """    if (showDatePicker) {
        com.example.ui.components.ModernDatePickerDialog(
            initialDateMillis = null,
            onDateSelected = { millis ->
                selectedDate = dateFormatter.format(java.util.Date(millis))
                showDatePicker = false
                if (exportPendingType == "PNG") {
                    exportPngLauncher.launch("rekap_tahfiz_${selectedRombel ?: "semua"}.png")
                } else if (exportPendingType == "PDF") {
                    exportPdfLauncher.launch("rekap_tahfiz_${selectedRombel ?: "semua"}.pdf")
                }
                exportPendingType = null
            },
            onDismiss = { showDatePicker = false }
        )
    }"""

content = content.replace(old_dialog, new_dialog)

with open("app/src/main/java/com/example/ui/screens/TahfizScreen.kt", "w") as f:
    f.write(content)
