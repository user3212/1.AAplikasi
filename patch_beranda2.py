import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # Find the start of the spacer before the photo block
    start_str = "Spacer(modifier = Modifier.width(16.dp))"
    idx_start = content.find(start_str, content.find("Greeting & Name"))
    
    if idx_start != -1:
        # The block ends before `// --- End of Header Row ---` or something similar. Let's find the `Row` block end.
        # The row starts at `Row(\n                    modifier = Modifier.fillMaxWidth()`
        # Look for the end of the Row block.
        idx_end = content.find("                }\n            }\n", idx_start)
        
        if idx_end != -1:
            # We want to keep the closing brace for the Row
            content = content[:idx_start] + "}\n" + content[idx_end+18:]

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('app/src/main/java/com/example/ui/screens/BerandaScreen.kt')
