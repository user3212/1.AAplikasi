import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar

@Composable
fun TimePickerField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Box(modifier = modifier.clickable {
        val calendar = Calendar.getInstance()
        if (value.isNotEmpty() && value.contains(":")) {
            val parts = value.split(":")
            calendar.set(Calendar.HOUR_OF_DAY, parts[0].toIntOrNull() ?: 0)
            calendar.set(Calendar.MINUTE, parts[1].toIntOrNull() ?: 0)
        }
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                onValueChange(String.format("%02d:%02d", hourOfDay, minute))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }) {
        // ...
    }
}
