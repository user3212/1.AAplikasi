sed -i '/fun setConfigJuz/a \
    fun getSantriJuz(santriId: Long): String = prefs.getString("santri_juz_$santriId", getConfigJuz()) ?: getConfigJuz()\
    fun setSantriJuz(santriId: Long, juz: String) { prefs.edit().putString("santri_juz_$santriId", juz).apply() }' app/src/main/java/com/example/ui/viewmodel/PesantrenViewModel.kt
