package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color

import androidx.compose.runtime.Composable

private val PesantrenDarkColorScheme = darkColorScheme(
    primary = TahfizAccent,
    secondary = Mapel1Accent,
    tertiary = Mapel2Accent,
    background = Color(0xFF121212), // Background layout bawaan hitam/gelap
    surface = Color(0xFF1E1E1E),    // Abu-abu Gelap (Graphite): #1E1E1E (RGB: 30, 30, 30) untuk container/kolom
    onBackground = Color(0xFFFFFFFF), // Teks berwarna putih terang di mode malam
    onSurface = Color(0xFFFFFFFF),    // Teks berwarna putih terang di mode malam
    onSurfaceVariant = Color(0xFFE2E8F0), // Teks sekunder terang di mode malam
    outline = Color(0xFF383838)
)

private val PesantrenColorScheme = lightColorScheme(
    primary = TahfizAccent,
    secondary = Mapel1Accent,
    tertiary = Mapel2Accent,
    background = BgPrimary,
    surface = CardBg,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = CardBorder
)

@Composable
fun PesantrenquTheme(
    isDarkMode: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (isDarkMode) PesantrenDarkColorScheme else PesantrenColorScheme,
        typography = Typography,
        content = content
    )
}
