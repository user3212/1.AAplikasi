import java.text.SimpleDateFormat
import java.util.Locale

fun main() {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val d = sdf.parse("25/08/2026")
    println(d)
}
