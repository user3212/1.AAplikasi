import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # Find the repetition config box
    target = r'''\s*// Repetition config.*?(?=</Column>)'''
    
    # Actually it's inside a Column. Let's just remove the Box containing OutlinedTextField for repetition.
    target = r'''\s*// Repetition config.*?\}\s*\}\s*\}\s*\}\s*\}\s*\}'''
    
    # A safer way to replace using re.DOTALL:
    target = r'''\s*// Repetition config\s*Box \{.*?\}\s*\}\s*\}'''
    
    content = re.sub(target, '', content, flags=re.DOTALL)

    # Also remove "var showRepetitionExpanded by remember { mutableStateOf(false) }"
    content = re.sub(r'\s*var showRepetitionExpanded by remember \{ mutableStateOf\(false\) \}', '', content)

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('app/src/main/java/com/example/ui/screens/JadwalScreen.kt')
