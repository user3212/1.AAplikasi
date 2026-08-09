import re

with open("app/src/main/java/com/example/ui/screens/SantriScreen.kt", "r") as f:
    content = f.read()

old_display = """                    if (filteredKelolaSantri.isEmpty()) {
                        Text(
                            text = "Belum ada siswa di daftar rombel",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        filteredKelolaSantri.forEach { santri ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Avatar
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(androidx.compose.foundation.shape.CircleShape)
                                        .background(Color(0xFFD1FAE5)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = santri.nama.take(1).uppercase(),
                                        style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFF047857), fontWeight = FontWeight.Bold)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = santri.nama,
                                        style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                                    )
                                    Text(
                                        text = "Rombel: ${santri.kelas}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.deleteSantri(santri) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Hapus Santri", tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }"""

new_display = """                    if (filteredKelolaSantri.isEmpty()) {
                        Text(
                            text = "Belum ada siswa di daftar rombel",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        val groupedSantri = filteredKelolaSantri.groupBy { it.kelas }.toSortedMap()
                        groupedSantri.forEach { (rombel, santriInRombel) ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Rombel: ${if (rombel.isBlank()) "Tanpa Rombel" else rombel}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF064E3B)),
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    santriInRombel.forEach { santri ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(MaterialTheme.colorScheme.surface)
                                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(androidx.compose.foundation.shape.CircleShape)
                                                    .background(Color(0xFFD1FAE5)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = santri.nama.take(1).uppercase(),
                                                    style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFF047857), fontWeight = FontWeight.Bold)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = santri.nama,
                                                    style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                                                )
                                            }
                                            IconButton(
                                                onClick = { viewModel.deleteSantri(santri) },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = "Hapus Santri", tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }"""

content = content.replace(old_display, new_display)

with open("app/src/main/java/com/example/ui/screens/SantriScreen.kt", "w") as f:
    f.write(content)
