with open("app/src/main/java/com/example/ui/screens/TahfizScreen.kt", "r") as f:
    content = f.read()

rombel_code = """
    var selectedRombel by remember { mutableStateOf<String?>(null) }
    
    val rombelOptions = remember(santriList) {
        listOf(null) + santriList.map { it.kelas }.distinct().filter { it.isNotBlank() }
    }"""

evaluasi_marker = "val tahfizRecords by viewModel.tahfizRecords.collectAsState()"

# Wait, this might match all three views! Let's be specific.
eval_func = """@Composable
fun EvaluasiKualitasView(viewModel: PesantrenViewModel, modifier: Modifier = Modifier) {
    val santriList by viewModel.santriList.collectAsState()
    val tahfizRecords by viewModel.tahfizRecords.collectAsState()
"""

new_eval = eval_func + rombel_code

content = content.replace(eval_func, new_eval)

with open("app/src/main/java/com/example/ui/screens/TahfizScreen.kt", "w") as f:
    f.write(content)
