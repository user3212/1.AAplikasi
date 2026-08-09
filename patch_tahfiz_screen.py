import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # Remove AturJuzHafalanSantriCard
    atur_str = '        item {\n            AturJuzHafalanSantriCard(viewModel = viewModel)\n        }\n'
    content = content.replace(atur_str, '')

    # Remove import AturJuzHafalanSantriCard
    content = content.replace('import com.example.ui.components.AturJuzHafalanSantriCard\n', '')

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('app/src/main/java/com/example/ui/screens/TahfizScreen.kt')
