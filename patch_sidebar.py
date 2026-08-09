import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # We need to collect state variables
    state_injection = """    val namaSekolahState by viewModel.namaSekolah.collectAsState()
    val alamatSekolahState by viewModel.alamatSekolah.collectAsState()
    val profilBase64 by viewModel.profilBase64.collectAsState()
    
    val displayNamaSekolah = namaSekolahState.takeIf { it.isNotBlank() } ?: "Pesantrenqu"
    val displayAlamatSekolah = alamatSekolahState.takeIf { it.isNotBlank() }?.uppercase() ?: "SUBULUS SALAM"
"""
    
    # inject state after `var infoExpanded by remember { mutableStateOf(false) }`
    content = content.replace("    var infoExpanded by remember { mutableStateOf(false) }", 
                              "    var infoExpanded by remember { mutableStateOf(false) }\n" + state_injection)
    
    
    old_icon_box = """                        Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))"""
    
    new_icon_box = """                        val cleanBase64 = if (profilBase64.contains(",")) profilBase64.substringAfter(",") else profilBase64
                        val imageBytes = if (cleanBase64.isNotBlank()) try { android.util.Base64.decode(cleanBase64, android.util.Base64.DEFAULT) } catch(e: Exception) { null } else null
                        val bitmap = if (imageBytes != null) try { android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size) } catch(e: Exception) { null } else null
                        if (bitmap != null) {
                            androidx.compose.foundation.Image(
                                bitmap = androidx.compose.ui.graphics.asImageBitmap(bitmap),
                                contentDescription = "Profile Picture",
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                        }"""
    content = content.replace(old_icon_box, new_icon_box)

    old_text_section = """Column {
                    val sharedPrefs = LocalContext.current.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    val namaSekolah = sharedPrefs.getString("nama_sekolah", "Pesantrenqu")?.takeIf { it.isNotBlank() } ?: "Pesantrenqu"
                    val alamatSekolah = sharedPrefs.getString("alamat_sekolah", "SUBULUS SALAM")?.takeIf { it.isNotBlank() }?.uppercase() ?: "SUBULUS SALAM"
                    Text(
                        text = namaSekolah,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground,
                            lineHeight = 20.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = alamatSekolah,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            lineHeight = 12.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }"""

    new_text_section = """Column {
                    Text(
                        text = displayNamaSekolah,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground,
                            lineHeight = 20.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = displayAlamatSekolah,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            lineHeight = 12.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }"""
                
    content = content.replace(old_text_section, new_text_section)
    
    with open(filepath, 'w') as f:
        f.write(content)

patch_file('app/src/main/java/com/example/ui/components/NavigationComponents.kt')
