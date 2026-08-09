import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # Fix the modifier chain
    bad_modifier = ".androidx.compose.ui.draw.scale(0.9f)"
    good_modifier = ".scale(0.9f)"
    content = content.replace(bad_modifier, good_modifier)
    
    # Add import if missing
    if "import androidx.compose.ui.draw.scale" not in content:
        content = content.replace("import androidx.compose.ui.graphics.Color", "import androidx.compose.ui.graphics.Color\nimport androidx.compose.ui.draw.scale")

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('app/src/main/java/com/example/ui/components/CommonComponents.kt')
