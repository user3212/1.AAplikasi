import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # Match from "// State for tracking already notified schedules" to "delay(15000) // check every 15 seconds\n        }\n    }"
    target = r'''\s*// State for tracking already notified schedules to prevent spam
\s*val notifiedSchedules = remember \{ mutableSetOf<String>\(\) \}
\s*// Real-time loop
\s*LaunchedEffect\(jadwalList\) \{
.*?\s*delay\(15000\) // check every 15 seconds
\s*\}
\s*\}'''

    content = re.sub(target, '', content, flags=re.DOTALL)

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('app/src/main/java/com/example/ui/screens/JadwalScreen.kt')
