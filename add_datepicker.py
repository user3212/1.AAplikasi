with open("app/src/main/java/com/example/ui/components/CommonComponents.kt", "r") as f:
    content = f.read()

datepicker_code = """
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ModernDatePickerDialog(
    initialDateMillis: Long?,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = androidx.compose.material3.rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis ?: System.currentTimeMillis()
    )
    
    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            androidx.compose.material3.TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { onDateSelected(it) }
                    onDismiss()
                }
            ) {
                androidx.compose.material3.Text("Pilih", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = androidx.compose.ui.graphics.Color(0xFF047857))
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                androidx.compose.material3.Text("Batal", color = androidx.compose.ui.graphics.Color.Gray)
            }
        },
        colors = androidx.compose.material3.DatePickerDefaults.colors(
            containerColor = androidx.compose.ui.graphics.Color.White
        )
    ) {
        androidx.compose.material3.DatePicker(
            state = datePickerState,
            colors = androidx.compose.material3.DatePickerDefaults.colors(
                titleContentColor = androidx.compose.ui.graphics.Color(0xFF064E3B),
                headlineContentColor = androidx.compose.ui.graphics.Color(0xFF047857),
                todayDateBorderColor = androidx.compose.ui.graphics.Color(0xFF10B981),
                todayContentColor = androidx.compose.ui.graphics.Color(0xFF10B981),
                selectedDayContainerColor = androidx.compose.ui.graphics.Color(0xFF047857),
                selectedDayContentColor = androidx.compose.ui.graphics.Color.White
            )
        )
    }
}
"""

if "fun ModernDatePickerDialog" not in content:
    content += datepicker_code

with open("app/src/main/java/com/example/ui/components/CommonComponents.kt", "w") as f:
    f.write(content)
