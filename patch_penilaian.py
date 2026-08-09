import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    row_target = r'''filteredSantri.forEach { santri ->
                                val currentScore = scoreMap\[santri.id\] \?= "0"
                                val currentNote = noteMap\[santri.id\] \?= ""

                                Row\(
                                    modifier = Modifier
                                        .fillMaxWidth\(\)
                                        .padding\(horizontal = 10.dp, vertical = 6.dp\),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy\(8.dp\)
                                \) {
                                    Row\(
                                        modifier = Modifier.weight\(1.2f\),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy\(8.dp\)
                                    \) {
                                        Box\(
                                            modifier = Modifier
                                                .size\(32.dp\)
                                                .clip\(androidx.compose.foundation.shape.CircleShape\)
                                                .background\(MaterialTheme.colorScheme.outline\),
                                            contentAlignment = Alignment.Center
                                        \) {
                                            Text\(
                                                text = santri.nama.take\(1\).uppercase\(\),
                                                style = MaterialTheme.typography.labelLarge.copy\(
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                \)
                                            \)
                                        }
                                        Text\('''

    row_replacement = '''filteredSantri.forEachIndexed { index, santri ->
                                val currentScore = scoreMap[santri.id] ?: "0"
                                val currentNote = noteMap[santri.id] ?: ""

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1.2f),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "${index + 1}.",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            ),
                                            modifier = Modifier.width(32.dp)
                                        )
                                        Text('''

    content = re.sub(row_target, row_replacement, content)

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('app/src/main/java/com/example/ui/screens/Mapel1Screen.kt')
patch_file('app/src/main/java/com/example/ui/screens/Mapel2Screen.kt')
