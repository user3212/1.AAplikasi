import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    export_target = r'''context\.contentResolver\.openOutputStream\(uri\)\?\.use \{ outputStream ->
\s*java\.io\.OutputStreamWriter\(outputStream\)\.use \{ writer ->
\s*writer\.write\("\{ \\"status\\": \\"success\\", \\"message\\": \\"Mock database backup data\\" \}"\)
\s*\}
\s*\}'''

    export_replacement = '''context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        java.io.OutputStreamWriter(outputStream).use { writer ->
                            val jsonString = viewModel.exportBackupJson()
                            writer.write(jsonString)
                        }
                    }'''
                    
    content = re.sub(export_target, export_replacement, content)

    import_target = r'''val importLauncher = rememberLauncherForActivityResult\(ActivityResultContracts\.OpenDocument\(\)\) \{ uri: android\.net\.Uri\? ->
\s*if \(uri != null\) \{
\s*viewModel\.showLoadingOverlay\("Mengimpor database\.\.\."\)
\s*\}
\s*\}'''

    import_replacement = '''val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: android.net.Uri? ->
        if (uri != null) {
            viewModel.showLoadingOverlay("Mengimpor database...")
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val reader = java.io.BufferedReader(java.io.InputStreamReader(inputStream))
                    val jsonString = reader.readText()
                    viewModel.importBackupJson(jsonString)
                }
            } catch (e: Exception) {
                viewModel.showToast("Gagal membaca file backup")
            } finally {
                viewModel.hideLoadingOverlay()
            }
        }
    }'''
    
    content = re.sub(import_target, import_replacement, content)

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('app/src/main/java/com/example/ui/screens/DatabaseScreen.kt')
