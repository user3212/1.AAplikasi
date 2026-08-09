import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    new_funcs = """    fun getSantriJuz(santriId: Long): String = prefs.getString("santri_juz_$santriId", getConfigJuz()) ?: getConfigJuz()
    fun setSantriJuz(santriId: Long, juz: String) { prefs.edit().putString("santri_juz_$santriId", juz).apply() }
    fun resetSantriJuz(santriId: Long) { prefs.edit().remove("santri_juz_$santriId").apply() }
    fun hasCustomJuz(santriId: Long): Boolean = prefs.contains("santri_juz_$santriId")"""

    content = re.sub(
        r'    fun getSantriJuz\(santriId: Long\): String = prefs\.getString\("santri_juz_\$santriId", getConfigJuz\(\)\) \?: getConfigJuz\(\)\n    fun setSantriJuz\(santriId: Long, juz: String\) \{ prefs\.edit\(\)\.putString\("santri_juz_\$santriId", juz\)\.apply\(\) \}',
        new_funcs,
        content
    )

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('app/src/main/java/com/example/ui/viewmodel/PesantrenViewModel.kt')
