import sys

def add_imports(filepath, imports_to_add):
    with open(filepath, 'r') as f:
        content = f.read()
    
    # Find the last import statement
    lines = content.split('\n')
    last_import_index = 0
    for i, line in enumerate(lines):
        if line.startswith('import '):
            last_import_index = i
            
    # Insert missing imports
    for imp in imports_to_add:
        if imp not in content:
            lines.insert(last_import_index + 1, imp)
            
    with open(filepath, 'w') as f:
        f.write('\n'.join(lines))

imports = [
    "import androidx.compose.ui.platform.LocalContext",
    "import androidx.activity.compose.rememberLauncherForActivityResult",
    "import androidx.activity.result.contract.ActivityResultContracts",
    "import androidx.compose.material.icons.filled.People",
    "import androidx.compose.material.icons.filled.Download",
    "import androidx.compose.material.icons.filled.UploadFile",
    "import androidx.compose.material.icons.filled.Edit",
    "import androidx.compose.material.icons.filled.ArrowBack",
    "import androidx.compose.material.icons.filled.Search",
    "import androidx.compose.material.icons.filled.Check",
    "import androidx.compose.material.icons.filled.Delete",
    "import androidx.compose.ui.graphics.Brush",
    "import java.io.BufferedReader",
    "import java.io.InputStreamReader"
]

add_imports('app/src/main/java/com/example/ui/screens/GenericMapelScreen.kt', imports)
add_imports('app/src/main/java/com/example/ui/screens/Mapel1Screen.kt', imports)
add_imports('app/src/main/java/com/example/ui/screens/SantriScreen.kt', imports)

print("Imports added")
