import sys

file_path = 'app/src/main/java/com/example/ui/screens/TahfizScreen.kt'
with open(file_path, 'r') as f:
    content = f.read()

# Add imports
if "import androidx.compose.ui.text.style.TextOverflow" not in content:
    content = content.replace("import androidx.compose.ui.text.font.FontWeight", "import androidx.compose.ui.text.font.FontWeight\nimport androidx.compose.ui.text.style.TextOverflow\nimport androidx.compose.ui.text.style.TextAlign")

# Replace text
old_row = """                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text(selectedSantri!!.nama, color = Color(0xFF6EE7B7), fontWeight = FontWeight.Bold)
                                    Text(lastRecord.surahJuz, color = Color.White, fontWeight = FontWeight.Bold)
                                    Text(lastRecord.ayatSelesai, color = Color(0xFF6EE7B7), fontWeight = FontWeight.Bold)
                                }"""

new_row = """                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text(selectedSantri!!.nama, color = Color(0xFF6EE7B7), fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(lastRecord.surahJuz, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(lastRecord.ayatSelesai, color = Color(0xFF6EE7B7), fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(0.5f), textAlign = TextAlign.End)
                                }"""

if old_row in content:
    content = content.replace(old_row, new_row)
else:
    print("Could not find the row to replace.")
    
with open(file_path, 'w') as f:
    f.write(content)
print("Patched TahfizScreen")
