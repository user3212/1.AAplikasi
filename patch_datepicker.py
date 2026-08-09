import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    old_picker = """@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
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
}"""

    new_picker = """@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ModernDatePickerDialog(
    initialDateMillis: Long?,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = androidx.compose.material3.rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis ?: System.currentTimeMillis()
    )

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = true,
            dismissOnClickOutside = true
        )
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = androidx.compose.ui.Modifier
                .fillMaxWidth()
                .shadow(24.dp, androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
                .background(
                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                        colors = listOf(
                            androidx.compose.ui.graphics.Color.White,
                            androidx.compose.ui.graphics.Color(0xFFF0FDF4)
                        )
                    )
                )
                .border(2.dp, androidx.compose.ui.graphics.Color(0xFF6EE7B7).copy(alpha=0.5f), androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
        ) {
            androidx.compose.foundation.layout.Column(
                modifier = androidx.compose.ui.Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                // Header 3D Effect
                androidx.compose.foundation.layout.Box(
                    modifier = androidx.compose.ui.Modifier
                        .fillMaxWidth()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    androidx.compose.ui.graphics.Color(0xFF10B981),
                                    androidx.compose.ui.graphics.Color(0xFF047857)
                                )
                            )
                        )
                        .padding(16.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    androidx.compose.material3.Text(
                        text = "Pilih Tanggal",
                        color = androidx.compose.ui.graphics.Color.White,
                        style = androidx.compose.material3.MaterialTheme.typography.titleMedium.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.3f),
                                offset = androidx.compose.ui.geometry.Offset(1f, 2f),
                                blurRadius = 3f
                            )
                        )
                    )
                }

                androidx.compose.material3.DatePicker(
                    state = datePickerState,
                    showModeToggle = false,
                    title = null,
                    headline = null,
                    colors = androidx.compose.material3.DatePickerDefaults.colors(
                        containerColor = androidx.compose.ui.graphics.Color.Transparent,
                        titleContentColor = androidx.compose.ui.graphics.Color(0xFF064E3B),
                        headlineContentColor = androidx.compose.ui.graphics.Color(0xFF047857),
                        todayDateBorderColor = androidx.compose.ui.graphics.Color(0xFF10B981),
                        todayContentColor = androidx.compose.ui.graphics.Color(0xFF10B981),
                        selectedDayContainerColor = androidx.compose.ui.graphics.Color(0xFF047857),
                        selectedDayContentColor = androidx.compose.ui.graphics.Color.White,
                        dayContentColor = androidx.compose.ui.graphics.Color.DarkGray,
                        currentYearContentColor = androidx.compose.ui.graphics.Color(0xFF047857),
                        selectedYearContainerColor = androidx.compose.ui.graphics.Color(0xFF047857)
                    ),
                    modifier = androidx.compose.ui.Modifier.padding(top = 8.dp)
                )

                androidx.compose.foundation.layout.Row(
                    modifier = androidx.compose.ui.Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    androidx.compose.material3.TextButton(
                        onClick = onDismiss,
                        modifier = androidx.compose.ui.Modifier.weight(1f)
                    ) {
                        androidx.compose.material3.Text("Batal", color = androidx.compose.ui.graphics.Color.Gray, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
                    }
                    
                    androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.width(12.dp))

                    androidx.compose.material3.Button(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { onDateSelected(it) }
                            onDismiss()
                        },
                        modifier = androidx.compose.ui.Modifier
                            .weight(1f)
                            .shadow(6.dp, androidx.compose.foundation.shape.RoundedCornerShape(12.dp)),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = androidx.compose.ui.graphics.Color(0xFF10B981)
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp)
                    ) {
                        androidx.compose.material3.Text("Pilih", color = androidx.compose.ui.graphics.Color.White, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }
                }
            }
        }
    }
}"""

    if old_picker in content:
        content = content.replace(old_picker, new_picker)
    else:
        print("old_picker not found!")

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('app/src/main/java/com/example/ui/components/CommonComponents.kt')
