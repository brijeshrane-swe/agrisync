package com.example.ui.theme

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
    primary = BentoDarkPrimary,
    onPrimary = BentoPurpleDark,
    primaryContainer = BentoDarkPrimaryContainer,
    onPrimaryContainer = BentoPurpleLight,
    secondary = BentoDarkPrimary,
    onSecondary = BentoPurpleDark,
    secondaryContainer = BentoDarkSurfaceElevated,
    onSecondaryContainer = BentoDarkTextPrimary,
    tertiary = BentoRoseContainer,
    onTertiary = BentoRoseOnContainer,
    background = BentoDarkBackground,
    onBackground = BentoDarkTextPrimary,
    surface = BentoDarkSurfaceCard,
    onSurface = BentoDarkTextPrimary,
    surfaceVariant = BentoDarkSurfaceElevated,
    onSurfaceVariant = BentoDarkTextSecondary,
    outline = BentoBorderStroke,
    outlineVariant = BentoBorderOutline
)

private val LightColorScheme = lightColorScheme(
    primary = BentoPurplePrimary,
    onPrimary = Color.White,
    primaryContainer = BentoPurpleContainer,
    onPrimaryContainer = BentoPurpleOnContainer,
    secondary = BentoPurpleDark,
    onSecondary = Color.White,
    secondaryContainer = BentoPurpleLight,
    onSecondaryContainer = BentoPurpleOnContainer,
    tertiary = BentoRoseContainer,
    onTertiary = BentoRoseOnContainer,
    background = BentoCanvasBackground,
    onBackground = BentoTextPrimary,
    surface = BentoSurfaceCard,
    onSurface = BentoTextPrimary,
    surfaceVariant = BentoSurfaceElevated,
    onSurfaceVariant = BentoTextSecondary,
    outline = BentoBorderStroke,
    outlineVariant = BentoBorderOutline
)

@Composable
fun AgriSyncTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
