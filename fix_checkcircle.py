import sys

def add_imports(filepath, imports_to_add):
    with open(filepath, 'r') as f:
        content = f.read()
    
    lines = content.split('\n')
    last_import_index = 0
    for i, line in enumerate(lines):
        if line.startswith('import '):
            last_import_index = i
            
    for imp in imports_to_add:
        if imp not in content:
            lines.insert(last_import_index + 1, imp)
            
    with open(filepath, 'w') as f:
        f.write('\n'.join(lines))

imports = [
    "import androidx.compose.material.icons.filled.CheckCircle"
]

add_imports('app/src/main/java/com/example/ui/screens/GenericMapelScreen.kt', imports)
add_imports('app/src/main/java/com/example/ui/screens/Mapel1Screen.kt', imports)
add_imports('app/src/main/java/com/example/ui/screens/SantriScreen.kt', imports)

print("Imports added")
