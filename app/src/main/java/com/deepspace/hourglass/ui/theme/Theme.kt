package com.deepspace.hourglass.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF76E4F7),
    secondary = Color(0xFFC5A3FF),
    tertiary = Color(0xFFFF8A5B),
    background = Color(0xFF0D0D1A),
    surface = Color(0xFF1A1A2E),
    onPrimary = Color(0xFF000000),
    onSecondary = Color(0xFF000000),
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF252540),
    onSurfaceVariant = Color(0xFF8888AA)
)

@Composable
fun DeepSpaceTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography(),
        content = content
    )
}
