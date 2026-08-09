import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # Import
    if 'import com.example.ui.screens.TahfizKonfigurasiScreen' not in content:
        content = content.replace(
            'import com.example.ui.screens.TahfizScreen',
            'import com.example.ui.screens.TahfizScreen\nimport com.example.ui.screens.TahfizKonfigurasiScreen'
        )

    # Route
    target = 'NavItem.TAHFIZ, NavItem.EVALUASI_TAHFIZ, NavItem.REKAP_TAHFIZ -> TahfizScreen(viewModel = viewModel, navItem = screen)'
    replacement = target + '\n                        NavItem.KONFIG_TAHFIZ -> TahfizKonfigurasiScreen(viewModel = viewModel)'
    content = content.replace(target, replacement)

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('app/src/main/java/com/example/MainActivity.kt')
