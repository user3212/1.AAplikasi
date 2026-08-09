import re

for filename in ["app/src/main/java/com/example/ui/screens/AttendanceScreen.kt", "app/src/main/java/com/example/ui/screens/Mapel1Screen.kt", "app/src/main/java/com/example/ui/screens/Mapel2Screen.kt"]:
    with open(filename, "r") as f:
        content = f.read()
    
    if "AttendanceScreen.kt" in filename:
        old_dialog = """    if (showDatePickerDialog) {
        // Date picker fallback logic can go here if needed, omitted for brevity.
        showDatePickerDialog = false
    }"""
        new_dialog = """    if (showDatePickerDialog) {
        com.example.ui.components.ModernDatePickerDialog(
            initialDateMillis = null,
            onDateSelected = { millis ->
                currentDate = dateFormatter.format(java.util.Date(millis))
            },
            onDismiss = { showDatePickerDialog = false }
        )
    }"""
        content = content.replace(old_dialog, new_dialog)
    else:
        # For Mapel screens, they have 2 date pickers: one for Attendance, one for Grade.
        # Actually, they might be using AlertDialog with CustomInputField for dates.
        # Let's just find and replace the AlertDialog logic if possible, or just leave it.
        pass

    with open(filename, "w") as f:
        f.write(content)

