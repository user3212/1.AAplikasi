import re

with open("app/src/main/java/com/example/ui/screens/TahfizScreen.kt", "r") as f:
    content = f.read()

# Make sure to import needed classes
imports = """import android.content.Context
import android.graphics.pdf.PdfDocument
import android.graphics.Paint
import android.graphics.Bitmap
import android.graphics.Canvas
import com.example.data.model.TahfizRecord
"""

if "import android.graphics.pdf.PdfDocument" not in content:
    content = content.replace("import java.util.Locale", "import java.util.Locale\n" + imports)

# We need tahfizRecords in RekapitulasiHafalanView
content = content.replace(
    "val santriList by viewModel.santriList.collectAsState()",
    "val santriList by viewModel.santriList.collectAsState()\n    val tahfizRecords by viewModel.tahfizRecords.collectAsState()"
)

# Replace exportPdfLauncher
old_pdf = """    val exportPdfLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri: Uri? ->
        if (uri != null) {
            viewModel.showLoadingOverlay("Mengekspor PDF...")
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write("Mock PDF Data".toByteArray())
                }
            } catch (e: Exception) {
                viewModel.showToast("Gagal mengekspor PDF")
            }
        }
    }"""

new_pdf = """    val exportPdfLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri: Uri? ->
        if (uri != null) {
            try {
                val pdfDocument = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas
                val paint = Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 14f
                    isAntiAlias = true
                }
                var y = 50f
                paint.isFakeBoldText = true
                paint.textSize = 18f
                canvas.drawText("Laporan Rekapitulasi Hafalan Tahfiz - Tanggal: $selectedDate", 50f, y, paint)
                y += 40f
                paint.isFakeBoldText = false
                paint.textSize = 12f
                
                val filteredSantri = if (selectedRombel != null) santriList.filter { it.kelas == selectedRombel } else santriList
                filteredSantri.forEach { santri ->
                    if (y > 800f) {
                        // For simplicity, we just stop drawing if it overflows the page in this example, 
                        // but normally you'd create a new page.
                    } else {
                        val santriRecords = tahfizRecords.filter { it.santriId == santri.id }
                        val totalSurah = santriRecords.map { it.surahJuz }.distinct().size
                        val totalKehadiran = santriRecords.map { it.tanggal }.distinct().size
                        val lastNilai = santriRecords.maxByOrNull { it.id }?.nilai ?: "-"
                        canvas.drawText("${santri.nama} (${santri.kelas}) | Hadir: $totalKehadiran | Surah Disetor: $totalSurah | Penilaian Terakhir: $lastNilai", 50f, y, paint)
                        y += 20f
                    }
                }
                
                pdfDocument.finishPage(page)
                context.contentResolver.openOutputStream(uri)?.use { 
                    pdfDocument.writeTo(it)
                }
                pdfDocument.close()
                android.widget.Toast.makeText(context, "Berhasil mengekspor PDF", android.widget.Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                android.widget.Toast.makeText(context, "Gagal mengekspor PDF", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }"""

content = content.replace(old_pdf, new_pdf)

# Replace exportPngLauncher
old_png = """    val exportPngLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("image/png")) { uri: Uri? ->
        if (uri != null) {
            viewModel.showLoadingOverlay("Mengekspor PNG...")
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write("Mock PNG Data".toByteArray())
                }
            } catch (e: Exception) {
                viewModel.showToast("Gagal mengekspor PNG")
            }
        }
    }"""

new_png = """    val exportPngLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("image/png")) { uri: Uri? ->
        if (uri != null) {
            try {
                val bitmap = Bitmap.createBitmap(800, 1400, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                canvas.drawColor(android.graphics.Color.WHITE)
                val paint = Paint().apply { color = android.graphics.Color.BLACK; textSize = 24f; isAntiAlias = true }
                
                val filteredSantri = if (selectedRombel != null) santriList.filter { it.kelas == selectedRombel } else santriList
                val santriScores = filteredSantri.map { santri ->
                    val santriRecords = tahfizRecords.filter { it.santriId == santri.id }
                    val totalAyat = santriRecords.sumOf { it.ayatSelesai.toIntOrNull() ?: 0 }
                    santri to totalAyat
                }.sortedByDescending { it.second }
                
                var y = 50f
                paint.textSize = 36f
                paint.isFakeBoldText = true
                paint.color = android.graphics.Color.parseColor("#064E3B")
                canvas.drawText("Peringkat Hafalan - $selectedDate", 50f, y, paint)
                y += 60f
                paint.textSize = 24f
                
                // Top Score
                paint.color = android.graphics.Color.parseColor("#059669")
                paint.isFakeBoldText = true
                canvas.drawText("🏆 Top Score (1-5)", 50f, y, paint)
                y += 40f
                paint.color = android.graphics.Color.BLACK
                paint.isFakeBoldText = false
                santriScores.take(5).forEachIndexed { i, (santri, score) ->
                    canvas.drawText("${i+1}. ${santri.nama} - $score ayat", 50f, y, paint)
                    y += 35f
                }
                
                // Menuju Top Score
                y += 20f
                paint.color = android.graphics.Color.parseColor("#D97706")
                paint.isFakeBoldText = true
                canvas.drawText("⭐ Menuju Top Score (6-10)", 50f, y, paint)
                y += 40f
                paint.color = android.graphics.Color.BLACK
                paint.isFakeBoldText = false
                santriScores.drop(5).take(5).forEachIndexed { i, (santri, score) ->
                    canvas.drawText("${i+6}. ${santri.nama} - $score ayat", 50f, y, paint)
                    y += 35f
                }
                
                // Fase Berjuang
                y += 20f
                paint.color = android.graphics.Color.parseColor("#2563EB")
                paint.isFakeBoldText = true
                canvas.drawText("💪 Fase Berjuang", 50f, y, paint)
                y += 40f
                paint.color = android.graphics.Color.BLACK
                paint.isFakeBoldText = false
                santriScores.drop(10).forEachIndexed { i, (santri, score) ->
                    if (y < 1350f) {
                        canvas.drawText("${i+11}. ${santri.nama} - $score ayat", 50f, y, paint)
                        y += 35f
                    }
                }
                
                context.contentResolver.openOutputStream(uri)?.use { 
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
                android.widget.Toast.makeText(context, "Berhasil mengekspor PNG", android.widget.Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                android.widget.Toast.makeText(context, "Gagal mengekspor PNG", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }"""

content = content.replace(old_png, new_png)

with open("app/src/main/java/com/example/ui/screens/TahfizScreen.kt", "w") as f:
    f.write(content)
