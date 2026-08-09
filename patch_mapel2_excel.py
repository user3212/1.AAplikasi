import sys

file_path = 'app/src/main/java/com/example/ui/screens/Mapel2Screen.kt'

with open(file_path, 'r') as f:
    content = f.read()

# Make sure we have the necessary imports
imports_to_add = [
    "import android.content.Intent",
    "import android.net.Uri",
    "import androidx.activity.compose.rememberLauncherForActivityResult",
    "import androidx.activity.result.contract.ActivityResultContracts",
    "import androidx.compose.ui.platform.LocalContext",
    "import java.io.BufferedReader",
    "import java.io.InputStreamReader"
]

for imp in imports_to_add:
    if imp not in content:
        content = content.replace("import androidx.compose.runtime.*", f"{imp}\nimport androidx.compose.runtime.*")

with open(file_path, 'w') as f:
    f.write(content)
print("Added imports")
