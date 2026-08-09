import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # We will match the Box up to the closing brace before the next Text(text = santri.nama,
    box_target = r'''Box\(
\s+modifier = Modifier
\s+.size\(32.dp\)
\s+.clip\(androidx.compose.foundation.shape.CircleShape\)
\s+.background\(MaterialTheme.colorScheme.outline\),
\s+contentAlignment = Alignment.Center
\s+\) \{
\s+Text\(
\s+text = santri.nama.take\(1\).uppercase\(\),
\s+style = MaterialTheme.typography.labelLarge.copy\(
\s+fontWeight = FontWeight.Bold,
\s+color = MaterialTheme.colorScheme.onSurfaceVariant
\s+\)
\s+\)
\s+\}'''

    box_replacement = '''Text(
                                            text = "${index + 1}.",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            ),
                                            modifier = Modifier.width(32.dp)
                                        )'''

    content = re.sub(box_target, box_replacement, content)

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('app/src/main/java/com/example/ui/screens/Mapel1Screen.kt')
patch_file('app/src/main/java/com/example/ui/screens/Mapel2Screen.kt')
