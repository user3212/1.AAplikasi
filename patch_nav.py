import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # Enum
    content = content.replace(
        'REKAP_TAHFIZ("Rekap Tahfiz", "Rekapitulasi Santri", Icons.Default.BarChart, Color(0xFF059669)),',
        'REKAP_TAHFIZ("Rekap Tahfiz", "Rekapitulasi Santri", Icons.Default.BarChart, Color(0xFF059669)),\n    KONFIG_TAHFIZ("Konfigurasi Tahfiz", "Konfigurasi Data", Icons.Default.Settings, Color(0xFF059669)),'
    )

    # Sidebar Sub Item
    content = content.replace(
        'SidebarSubItemRow("Rekapitulasi", Icons.Default.BarChart, selectedItem == NavItem.REKAP_TAHFIZ) { onItemSelected(NavItem.REKAP_TAHFIZ) }',
        'SidebarSubItemRow("Rekapitulasi", Icons.Default.BarChart, selectedItem == NavItem.REKAP_TAHFIZ) { onItemSelected(NavItem.REKAP_TAHFIZ) }\n                            SidebarSubItemRow("Konfigurasi", Icons.Default.Settings, selectedItem == NavItem.KONFIG_TAHFIZ) { onItemSelected(NavItem.KONFIG_TAHFIZ) }'
    )

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('app/src/main/java/com/example/ui/components/NavigationComponents.kt')
