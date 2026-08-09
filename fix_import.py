import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    content = content.replace('import com.example.util.QuranData', 'import com.example.data.model.QuranData')

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('app/src/main/java/com/example/ui/screens/TahfizKonfigurasiScreen.kt')
