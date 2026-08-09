import re

with open("app/src/main/java/com/example/ui/components/NavigationComponents.kt", "r") as f:
    content = f.read()

old_code = """                            Text(
                                text = "Update Aplikasi",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF3B82F6),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    textDecoration = TextDecoration.Underline
                                ),
                                modifier = Modifier.clickable {
                                    uriHandler.openUri("https://lynk.id/aplikasiqu")
                                }
                            )"""

new_code = """                            Row(
                                modifier = Modifier.clickable {
                                    uriHandler.openUri("https://lynk.id/aplikasiqu")
                                },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                androidx.compose.foundation.Image(
                                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_g_icon),
                                    contentDescription = "App Icon",
                                    modifier = Modifier.size(16.dp).clip(RoundedCornerShape(4.dp))
                                )
                                Text(
                                    text = "Update Aplikasi",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF3B82F6),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        textDecoration = TextDecoration.Underline
                                    )
                                )
                            }"""

content = content.replace(old_code, new_code)

with open("app/src/main/java/com/example/ui/components/NavigationComponents.kt", "w") as f:
    f.write(content)
