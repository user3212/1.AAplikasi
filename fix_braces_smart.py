import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # Split into lines
    lines = content.split('\n')
    
    # We want to remove extra braces right before `@Composable\nfun RiayahEmblemLogo`
    # Let's count open/close braces from start to `fun RiayahEmblemLogo`
    
    idx_logo = -1
    for i, line in enumerate(lines):
        if 'fun RiayahEmblemLogo' in line:
            idx_logo = i
            break
            
    if idx_logo != -1:
        # Count braces up to idx_logo
        open_b = 0
        close_b = 0
        for i in range(idx_logo):
            open_b += lines[i].count('{')
            close_b += lines[i].count('}')
            
        diff = close_b - open_b
        
        # If diff > 0, we have extra closing braces in the HeroBerandaBanner function
        if diff > 0:
            for i in range(idx_logo - 1, -1, -1):
                if diff == 0:
                    break
                if '}' in lines[i]:
                    lines[i] = lines[i].replace('}', '', 1)
                    diff -= 1

    with open(filepath, 'w') as f:
        f.write('\n'.join(lines))

patch_file('app/src/main/java/com/example/ui/screens/BerandaScreen.kt')
