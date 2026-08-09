import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # 1. Collect importResultLog
    if 'val importResultLog by viewModel.importResultLog.collectAsState()' not in content:
        content = content.replace("fun DatabaseScreen(viewModel: PesantrenViewModel, modifier: Modifier = Modifier) {",
                                  "fun DatabaseScreen(viewModel: PesantrenViewModel, modifier: Modifier = Modifier) {\n    val importResultLog by viewModel.importResultLog.collectAsState()")

    # 2. Add Dialog
    dialog_ui = """
    if (importResultLog != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearImportResultLog() },
            title = {
                Text("Hasil Impor Database", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(text = importResultLog!!, fontSize = 14.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.clearImportResultLog() }) {
                    Text("Tutup", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
"""
    if 'Hasil Impor Database' not in content:
        content = content.replace("if (showFirstConfirmDialog) {", dialog_ui + "\n    if (showFirstConfirmDialog) {")

    # Add missing imports for verticalScroll and rememberScrollState
    if 'import androidx.compose.foundation.verticalScroll' not in content:
        content = content.replace('import androidx.compose.foundation.background',
                                  'import androidx.compose.foundation.background\nimport androidx.compose.foundation.verticalScroll\nimport androidx.compose.foundation.rememberScrollState')

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('app/src/main/java/com/example/ui/screens/DatabaseScreen.kt')
