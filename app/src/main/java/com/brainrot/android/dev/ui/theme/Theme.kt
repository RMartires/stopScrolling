package com.brainrot.android.dev.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PurpleDeep = Color(0xFF1A1028)
val PurpleSurface = Color(0xFF261736)
val PurpleCard = Color(0xFF322044)
val AccentOrange = Color(0xFFFF8A5B)
val AccentLilac = Color(0xFFB794F6)
val TextMuted = Color(0xFFBCA8D6)

private val DarkColors = darkColorScheme(
    primary = AccentOrange,
    onPrimary = PurpleDeep,
    secondary = AccentLilac,
    onSecondary = PurpleDeep,
    background = PurpleDeep,
    onBackground = Color(0xFFF6EEFF),
    surface = PurpleSurface,
    onSurface = Color(0xFFF6EEFF),
    surfaceVariant = PurpleCard,
    onSurfaceVariant = TextMuted,
    outline = Color(0xFF4E3A66),
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF6D28D9),
    onPrimary = Color.White,
    secondary = AccentOrange,
    background = Color(0xFFF8F5FF),
    onBackground = PurpleDeep,
    surface = Color.White,
    onSurface = PurpleDeep,
    surfaceVariant = Color(0xFFEDE6FA),
    onSurfaceVariant = Color(0xFF5B4A72),
)

@Composable
fun StopScrollingTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
