import re

with open("app/src/main/java/com/example/ui/screens/TahfizScreen.kt", "r") as f:
    lines = f.readlines()

new_lines = []
for idx, line in enumerate(lines):
    if "val tahfizRecords by viewModel.tahfizRecords.collectAsState()" in line and "val tahfizRecords by viewModel.tahfizRecords.collectAsState()" in lines[idx-1]:
        continue # Skip duplicate
    new_lines.append(line)

content = "".join(new_lines)

# Move selectedRombel up in RekapitulasiHafalanView
rombel_decl = """    var selectedRombel by remember { mutableStateOf<String?>(null) }
    
    val rombelOptions = remember(santriList) {
        listOf(null) + santriList.map { it.kelas }.distinct().filter { it.isNotBlank() }
    }"""

# Remove from bottom
content = content.replace(rombel_decl, "")

# Add after exportPendingType
target = "    var exportPendingType by remember { mutableStateOf<String?>(null) }"
content = content.replace(target, target + "\n" + rombel_decl)

with open("app/src/main/java/com/example/ui/screens/TahfizScreen.kt", "w") as f:
    f.write(content)
