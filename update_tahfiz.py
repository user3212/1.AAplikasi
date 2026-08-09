with open("app/src/main/java/com/example/ui/screens/TahfizScreen.kt", "r") as f:
    content = f.read()

insert1 = """    var selectedRombel by remember { mutableStateOf<String?>(null) }
    
    val rombelOptions = remember(santriList) {
        listOf(null) + santriList.map { it.kelas }.distinct().filter { it.isNotBlank() }
    }
    
    val filteredSantriList = remember(santriList, selectedRombel) {
        if (selectedRombel == null) santriList else santriList.filter { it.kelas == selectedRombel }
    }
    
    var selectedSantri by remember"""

content = content.replace("    var selectedSantri by remember", insert1)

insert2 = """                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Class, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("PILIH ROMBEL", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF4B5563)))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        CustomDropdown(
                            label = "",
                            options = rombelOptions,
                            selectedOption = selectedRombel,
                            onOptionSelected = { 
                                selectedRombel = it
                                selectedSantri = null
                            },
                            optionToString = { it ?: "Semua Rombel" },
                            focusAccentColor = Color(0xFF10B981),
                            placeholder = "-- Pilih Rombel --"
                        )
                    }

                    Column {"""

content = content.replace("                    Column {\n                        Row(verticalAlignment = Alignment.CenterVertically) {\n                            Icon(Icons.Default.PersonOutline, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))\n                            Spacer(modifier = Modifier.width(8.dp))\n                            Text(\"PILIH SANTRI\", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF4B5563)))", insert2 + "\n                        Row(verticalAlignment = Alignment.CenterVertically) {\n                            Icon(Icons.Default.PersonOutline, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))\n                            Spacer(modifier = Modifier.width(8.dp))\n                            Text(\"PILIH SANTRI\", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF4B5563)))")

content = content.replace("options = santriList,", "options = filteredSantriList,")

with open("app/src/main/java/com/example/ui/screens/TahfizScreen.kt", "w") as f:
    f.write(content)
