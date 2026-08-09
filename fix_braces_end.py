import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    lines = content.split('\n')
    
    idx_logo = -1
    for i, line in enumerate(lines):
        if 'fun RiayahEmblemLogo' in line:
            idx_logo = i
            break
            
    if idx_logo != -1:
        # Count braces from idx_logo to end
        open_b = 0
        close_b = 0
        for i in range(idx_logo, len(lines)):
            open_b += lines[i].count('{')
            close_b += lines[i].count('}')
            
        diff = open_b - close_b
        
        # If diff > 0, we are missing closing braces at the end
        if diff > 0:
            for _ in range(diff):
                lines.append("}")

    with open(filepath, 'w') as f:
        f.write('\n'.join(lines))

patch_file('app/src/main/java/com/example/ui/screens/BerandaScreen.kt')
