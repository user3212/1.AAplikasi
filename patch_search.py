import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # Make search slim and spacing smaller
    # We find the OutlinedTextField block and update its modifier, and also reduce the Arrangement.spacedBy
    content = re.sub(
        r'modifier = Modifier\.fillMaxWidth\(\),(\s+)singleLine = true,',
        r'modifier = Modifier.fillMaxWidth().height(46.dp),\1singleLine = true,',
        content
    )

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('app/src/main/java/com/example/ui/screens/AttendanceScreen.kt')
patch_file('app/src/main/java/com/example/ui/screens/Mapel1Screen.kt')
patch_file('app/src/main/java/com/example/ui/screens/Mapel2Screen.kt')
