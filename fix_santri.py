import re

with open("app/src/main/java/com/example/ui/screens/SantriScreen.kt", "r") as f:
    content = f.read()

# Add imports if not present
if "import android.net.Uri" not in content:
    content = content.replace("import androidx.compose.runtime.*", "import androidx.compose.runtime.*\nimport android.net.Uri\nimport androidx.activity.compose.rememberLauncherForActivityResult\nimport androidx.activity.result.contract.ActivityResultContracts\nimport androidx.compose.ui.platform.LocalContext")

# Replace Excel dialog with file pickers
old_excel = """    var showExcelDialog by remember { mutableStateOf(false) }

    var kelolaNama by remember { mutableStateOf("") }
    var kelolaRombel by remember { mutableStateOf("") }
    var kelolaSearch by remember { mutableStateOf("") }
    var inputFormExpanded by remember { mutableStateOf(true) }

    val filteredKelolaSantri = remember(santriList, kelolaSearch) {
        santriList.filter {
            kelolaSearch.isEmpty() || it.nama.contains(kelolaSearch, ignoreCase = true)
        }.sortedBy { it.nama }
    }

    if (showExcelDialog) {
        ExcelSmartParserDialog(
            title = "Data Santri",
            subtitle = "Unggah Excel Data Siswa",
            samplePresets = listOf(
                "Sample Rombel 7A-7C" to "Ahmad Yazid, 7A\\nAl Faiz Zoka, 7A\\nAnanda Ryla Kenzi, 7B\\nArifqy, 7B\\nFadlan Al Fatirh, 7C"
            ),
            onDismiss = { showExcelDialog = false },
            onImport = { pairs ->
                viewModel.importSantriBatch(pairs)
            }
        )
    }"""

new_excel = """    var kelolaNama by remember { mutableStateOf("") }
    var kelolaRombel by remember { mutableStateOf("") }
    var kelolaSearch by remember { mutableStateOf("") }
    var inputFormExpanded by remember { mutableStateOf(true) }

    val filteredKelolaSantri = remember(santriList, kelolaSearch) {
        santriList.filter {
            kelolaSearch.isEmpty() || it.nama.contains(kelolaSearch, ignoreCase = true)
        }.sortedBy { it.nama }
    }
    
    val context = LocalContext.current
    val pickSpreadsheetLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val contentStr = inputStream.bufferedReader().use { it.readText() }
                    val lines = contentStr.split("\\n")
                    val pairs = mutableListOf<Pair<String, String>>()
                    for (line in lines.drop(1)) { // Skip header
                        val parts = line.split(",")
                        if (parts.size >= 2) {
                            val nama = parts[0].trim().removeSurrounding("\\"")
                            val rombel = parts[1].trim().removeSurrounding("\\"")
                            if (nama.isNotEmpty() && rombel.isNotEmpty()) {
                                pairs.add(nama to rombel)
                            }
                        }
                    }
                    if (pairs.isNotEmpty()) {
                        viewModel.importSantriBatch(pairs)
                        android.widget.Toast.makeText(context, "Berhasil mengimpor ${pairs.size} santri", android.widget.Toast.LENGTH_SHORT).show()
                    } else {
                        android.widget.Toast.makeText(context, "Data kosong atau format salah", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                android.widget.Toast.makeText(context, "Gagal membaca file: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    val downloadTemplateLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write("Nama,Rombel\\nFulan,7A\\nFulana,7B".toByteArray())
                }
                android.widget.Toast.makeText(context, "Template berhasil diunduh", android.widget.Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                android.widget.Toast.makeText(context, "Gagal mengunduh template", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }"""

content = content.replace(old_excel, new_excel)

# Replace the click actions
content = content.replace("""viewModel.showToast("Template Excel telah diunduh")""", """downloadTemplateLauncher.launch("Template_Data_Santri.csv")""")
content = content.replace("""showExcelDialog = true""", """pickSpreadsheetLauncher.launch("*/*")""")

with open("app/src/main/java/com/example/ui/screens/SantriScreen.kt", "w") as f:
    f.write(content)
