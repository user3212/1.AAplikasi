import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # Update MobileHeader signature
    old_sig = """fun MobileHeader(
    currentTitle: String,
    onOpenDrawer: () -> Unit,
    onProfileClick: () -> Unit,
    isProfileVerified: Boolean,
    modifier: Modifier = Modifier,"""
    new_sig = """fun MobileHeader(
    currentTitle: String,
    onOpenDrawer: () -> Unit,
    onProfileClick: () -> Unit,
    isProfileVerified: Boolean,
    profilBase64: String = "",
    modifier: Modifier = Modifier,"""
    
    content = content.replace(old_sig, new_sig)
    
    # Update Icon in MobileHeader
    old_icon = """                    // Main 3D Icon Background
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center)
                            .shadow(6.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF34D399), Color(0xFF047857))
                                )
                            )
                            .border(1.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profil",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }"""
    
    new_icon = """                    // Main 3D Icon Background
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center)
                            .shadow(6.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF34D399), Color(0xFF047857))
                                )
                            )
                            .border(1.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val cleanBase64 = if (profilBase64.contains(",")) profilBase64.substringAfter(",") else profilBase64
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
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profil",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }"""
    content = content.replace(old_icon, new_icon)

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('app/src/main/java/com/example/ui/components/NavigationComponents.kt')
