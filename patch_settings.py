import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # Remove state
    content = re.sub(r'    var isKonfigurasiJuzExpanded by remember \{ mutableStateOf\(true\) \}\n', '', content)
    
    # Remove the whole Konfigurasi Juz Al-Qur'an item block
    # It starts from `item {` and ends before `// Section khusus Atur Juz Hafalan Santri`
    # Let's just use regex or manual replace.
    # We want to remove from `item {` before `PesantrenCard {` containing `isKonfigurasiJuzExpanded` up to `// Section khusus Atur Juz Hafalan Santri`
    start_str = '        item {\n            PesantrenCard {\n                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {\n                    Row(\n                        modifier = Modifier\n                            .fillMaxWidth()\n                            .clickable { isKonfigurasiJuzExpanded = !isKonfigurasiJuzExpanded },'
    
    idx_start = content.find(start_str)
    if idx_start != -1:
        # find the next `        item {` which is `// Section khusus Atur Juz Hafalan Santri`
        idx_end = content.find('        // Section khusus Atur Juz Hafalan Santri', idx_start)
        if idx_end != -1:
            content = content[:idx_start] + content[idx_end:]

    # Remove AturJuzHafalanSantriCard
    atur_str = '        // Section khusus Atur Juz Hafalan Santri\n        item {\n            AturJuzHafalanSantriCard(viewModel = viewModel)\n        }\n'
    content = content.replace(atur_str, '')

    # Remove import AturJuzHafalanSantriCard
    content = content.replace('import com.example.ui.components.AturJuzHafalanSantriCard\n', '')
    
    # Also remove QuranData import if not used elsewhere in SettingsScreen
    if 'import com.example.util.QuranData' in content and content.count('QuranData') == 1:
        content = content.replace('import com.example.util.QuranData\n', '')

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('app/src/main/java/com/example/ui/screens/SettingsScreen.kt')
