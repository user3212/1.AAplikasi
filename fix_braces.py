import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # Find if we have an extra brace at the end.
    lines = content.split('\n')
    
    # We can just run a quick bracket matching
    open_count = 0
    close_count = 0
    for line in lines:
        open_count += line.count('{')
        close_count += line.count('}')
        
    diff = close_count - open_count
    if diff > 0:
        # We have extra closing braces, let's remove them from the end
        for i in range(len(lines)-1, -1, -1):
            if diff == 0:
                break
            if '}' in lines[i] and 'fun ' not in lines[i] and 'class ' not in lines[i]:
                lines[i] = lines[i].replace('}', '', 1)
                diff -= 1

    with open(filepath, 'w') as f:
        f.write('\n'.join(lines))

patch_file('app/src/main/java/com/example/ui/screens/BerandaScreen.kt')
