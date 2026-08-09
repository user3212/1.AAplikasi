import sys

def add_star_import(filepath):
    with open(filepath, 'r') as f:
        content = f.read()
    
    if "import androidx.compose.material.icons.filled.*" not in content:
        content = content.replace("import androidx.compose.material.icons.Icons", "import androidx.compose.material.icons.Icons\nimport androidx.compose.material.icons.filled.*")
        with open(filepath, 'w') as f:
            f.write(content)

add_star_import('app/src/main/java/com/example/ui/screens/GenericMapelScreen.kt')
add_star_import('app/src/main/java/com/example/ui/screens/Mapel1Screen.kt')
add_star_import('app/src/main/java/com/example/ui/screens/SantriScreen.kt')

print("Star imports added")
