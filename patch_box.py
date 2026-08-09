import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # The Box containing the icon abjad in Penilaian
    box_target = r'''Box\(
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
                                                }'''

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
