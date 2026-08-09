import re

with open("app/src/main/java/com/example/ui/screens/TahfizScreen.kt", "r") as f:
    content = f.read()

# Replace android.app.DatePickerDialog usage in SetoranHafalanView
old_date = """                        .clickable {
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
                        }"""

new_date = """                        .clickable { showDatePickerDialog = true }"""

# Before the Box with clickable, add `var showDatePickerDialog by remember { mutableStateOf(false) }`
# Wait, let's just insert it before the Box.
# I'll just use regex to inject it.

if "var showDatePickerDialog by remember" not in content[:3000]:
    content = content.replace("val context = LocalContext.current", "val context = LocalContext.current\n    var showDatePickerDialog by remember { mutableStateOf(false) }\n    \n    if (showDatePickerDialog) {\n        com.example.ui.components.ModernDatePickerDialog(\n            initialDateMillis = null,\n            onDateSelected = { millis ->\n                tanggal = dateFormatter.format(java.util.Date(millis))\n            },\n            onDismiss = { showDatePickerDialog = false }\n        )\n    }", 1)

content = content.replace(old_date, new_date)

with open("app/src/main/java/com/example/ui/screens/TahfizScreen.kt", "w") as f:
    f.write(content)
