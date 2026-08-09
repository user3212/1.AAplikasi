import sys

def patch_file(filepath):
    with open(filepath, 'r') as f:
        lines = f.readlines()

    # Find where @Composable fun HeroBerandaBanner starts
    start_idx = -1
    for i, line in enumerate(lines):
        if '@Composable' in line and 'fun HeroBerandaBanner' in lines[i+1] if i+1 < len(lines) else False:
            start_idx = i
            break
            
    # Find where @Composable fun RiayahEmblemLogo starts
    end_idx = -1
    for i, line in enumerate(lines):
        if '@Composable' in line and 'fun RiayahEmblemLogo' in lines[i+1] if i+1 < len(lines) else False:
            end_idx = i
            break
            
    if start_idx == -1 or end_idx == -1:
        print("Could not find start or end index")
        return
        
    print(f"Replacing lines {start_idx} to {end_idx - 1}")
    
    new_code = """@Composable
fun HeroBerandaBanner(viewModel: PesantrenViewModel) {
    val profilBase64 by viewModel.profilBase64.collectAsState()
    val namaGuruRaw by viewModel.namaGuru.collectAsState()
    val jenisKelamin by viewModel.jenisKelamin.collectAsState()
    val namaGuru = if (!namaGuruRaw.isNullOrBlank()) namaGuruRaw else "Guru"
    val sapaan = if (jenisKelamin == "Perempuan") "Ibu" else "Bapak"
    val namaSapaan = "$sapaan $namaGuru"

    var currentTime by remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while(true) {
            currentTime = LocalDateTime.now()
            delay(1000)
        }
    }

    val hour = currentTime.hour
    val greeting = when {
        hour in 5..10 -> "Selamat Pagi,"
        hour in 11..14 -> "Selamat Siang,"
        hour in 15..17 -> "Selamat Sore,"
        else -> "Selamat Malam,"
    }

    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale("id", "ID"))
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale("id", "ID"))
    
    val timeString = currentTime.format(timeFormatter)
    val dateString = currentTime.format(dateFormatter)

    Surface(
        color = Color.Transparent,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 12.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF047857),
                            Color(0xFF064E3B)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Greeting & Name
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        Text(
                            text = greeting,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        )
                        Text(
                            text = namaSapaan,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 32.sp,
                                lineHeight = 36.sp,
                                shadow = androidx.compose.ui.graphics.Shadow(
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                    offset = androidx.compose.ui.geometry.Offset(2f, 4f),
                                    blurRadius = 4f
                                )
                            )
                        )
                        Text(
                            text = "Selamat datang di aplikasi Guruqu",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = Color(0xFFD1FAE5),
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                // Digital Clock and Date Container
                Surface(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val isDarkMode by viewModel.isDarkMode.collectAsState()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF0F766E).copy(alpha = 0.9f),
                                        Color(0xFF047857).copy(alpha = 0.9f)
                                    )
                                )
                            )
                            .border(1.dp, Color(0xFF6EE7B7).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(vertical = 20.dp, horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            // Dark Mode Toggle
                            Box(
                                modifier = Modifier
                                    .height(44.dp) // Matching time text height approximately
                                    .width(28.dp)
                                    .shadow(6.dp, RoundedCornerShape(14.dp))
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            colors = if (isDarkMode) listOf(MaterialTheme.colorScheme.onBackground, MaterialTheme.colorScheme.onBackground)
                                                    else listOf(Color(0xFF93C5FD), Color(0xFF3B82F6))
                                        )
                                    )
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        viewModel.toggleDarkMode()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                val thumbOffset by animateFloatAsState(
                                    targetValue = if (isDarkMode) -10f else 10f,
                                    animationSpec = tween(300),
                                    label = "thumbOffset"
                                )
                                // Track icons
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(vertical = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Icon(Icons.Default.DarkMode, contentDescription = null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(12.dp))
                                    Icon(Icons.Default.LightMode, contentDescription = null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(12.dp))
                                }
                                // Thumb
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .offset(y = thumbOffset.dp)
                                        .shadow(4.dp, CircleShape)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                        contentDescription = null,
                                        tint = if (isDarkMode) MaterialTheme.colorScheme.onBackground else Color(0xFF3B82F6),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = timeString,
                                    style = MaterialTheme.typography.displayMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 36.sp,
                                        letterSpacing = 2.sp,
                                        shadow = androidx.compose.ui.graphics.Shadow(
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                                            offset = androidx.compose.ui.geometry.Offset(0f, 4f),
                                            blurRadius = 8f
                                        )
                                    )
                                )
                                Text(
                                    text = dateString,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color(0xFFA7F3D0),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
"""

    new_lines = lines[:start_idx] + [new_code + "\n"] + lines[end_idx:]
    with open(filepath, 'w') as f:
        f.writelines(new_lines)

patch_file('app/src/main/java/com/example/ui/screens/BerandaScreen.kt')
