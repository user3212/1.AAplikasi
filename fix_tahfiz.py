import re

with open("app/src/main/java/com/example/ui/screens/TahfizScreen.kt", "r") as f:
    content = f.read()

# 1. Remove ALL selectedRombel declarations globally
content = re.sub(r'\s*var selectedRombel by remember \{ mutableStateOf<String\?>\(null\) \}\n\s*val rombelOptions = remember\(santriList\) \{\n\s*listOf\(null\) \+ santriList\.map \{ it\.kelas \}\.distinct\(\)\.filter \{ it\.isNotBlank\(\) \}\n\s*\}', '', content)

# 2. Insert it back at exactly two places: inside SetoranHafalanView and RekapitulasiHafalanView
setoran_marker = "    var showDatePickerDialog by remember { mutableStateOf(false) }"
rombel_code = """
    var selectedRombel by remember { mutableStateOf<String?>(null) }
    
    val rombelOptions = remember(santriList) {
        listOf(null) + santriList.map { it.kelas }.distinct().filter { it.isNotBlank() }
    }"""
content = content.replace(setoran_marker, setoran_marker + rombel_code)

rekap_marker = "    var exportPendingType by remember { mutableStateOf<String?>(null) }"
content = content.replace(rekap_marker, rekap_marker + rombel_code)

# 3. filteredSantriList in SetoranHafalanView was probably also messed up?
# Let's check if it exists:
if "val filteredSantriList = remember(santriList, selectedRombel)" not in content:
    content = content.replace(setoran_marker + rombel_code, setoran_marker + rombel_code + """
    val filteredSantriList = remember(santriList, selectedRombel) {
        if (selectedRombel == null) santriList else santriList.filter { it.kelas == selectedRombel }
    }
""")

# 4. In RekapitulasiHafalanView, `filteredSantriList` was defined. Let's make sure it doesn't conflict.
# The original code had:
# val filteredSantriList = remember(santriList, selectedRombel, filterSort) {
#    val list = if (selectedRombel == null) santriList else santriList.filter { it.kelas == selectedRombel }
# Let's ensure it's there.

with open("app/src/main/java/com/example/ui/screens/TahfizScreen.kt", "w") as f:
    f.write(content)
