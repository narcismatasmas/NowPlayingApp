package com.example.nowplaying.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF5B04),
    secondary = Color(0xFF075056),
    tertiary = Color(0xFFE4EEF0),
    background = Color(0xFF16232A),
    surface = Color(0xFF075056),
    onPrimary = Color(0xFF16232A),
    onSecondary = Color(0xFFE4EEF0),
    onTertiary = Color(0xFF075056),
    onBackground = Color(0xFFE4EEF0),
    onSurface = Color(0xFFE4EEF0)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFFF5B04),
    secondary = Color(0xFF075056),
    tertiary = Color(0xFFE4EEF0),
    background = Color(0xFF16232A),
    surface = Color(0xFF075056),
    onPrimary = Color(0xFF16232A),
    onSecondary = Color(0xFFE4EEF0),
    onTertiary = Color(0xFF075056),
    onBackground = Color(0xFFE4EEF0),
    onSurface = Color(0xFFE4EEF0)
)

@Composable
fun NowPlayingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}