import re

for filename in ["app/src/main/java/com/example/ui/screens/Mapel1Screen.kt", "app/src/main/java/com/example/ui/screens/Mapel2Screen.kt"]:
    with open(filename, "r") as f:
        content = f.read()

    # Find the block: `if (showDatePickerDialog) { ... }`
    # We will use regex to find the `AlertDialog` and replace it
    pattern = re.compile(r'if \(showDatePickerDialog\) \{.*?AlertDialog.*?\}\n        \)', re.DOTALL)
    
    new_dialog = """if (showDatePickerDialog) {
        com.example.ui.components.ModernDatePickerDialog(
            initialDateMillis = null,
            onDateSelected = { millis ->
                currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(java.util.Date(millis))
            },
            onDismiss = { showDatePickerDialog = false }
        )"""

    content = pattern.sub(new_dialog, content)
    
    # Remove any stray tempDateInput variables
    content = re.sub(r'var tempDateInput by remember \{ mutableStateOf\(formatDbToDisplayDate\(currentDate\)\) \}\n?', '', content)
    content = re.sub(r'tempDateInput = formatDbToDisplayDate\(currentDate\)\n?', '', content)

    with open(filename, "w") as f:
        f.write(content)
