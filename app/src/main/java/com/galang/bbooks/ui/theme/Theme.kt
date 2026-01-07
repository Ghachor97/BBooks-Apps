package com.galang.bbooks.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Liquid Glass Dark Color Scheme
private val LiquidGlassDarkScheme = darkColorScheme(
    primary = PurpleAccent,
    onPrimary = Color.White,
    primaryContainer = PurpleDark,
    onPrimaryContainer = PurpleLight,
    
    secondary = BlueAccent,
    onSecondary = Color.White,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = TextPrimary,
    
    tertiary = StatusOrange,
    onTertiary = Color.Black,
    
    background = DarkBackground,
    onBackground = TextPrimary,
    
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    
    error = StatusRed,
    onError = Color.White,
    errorContainer = StatusRedLight,
    onErrorContainer = Color.Black,
    
    outline = GlassBorder,
    outlineVariant = GlassWhite
)

// Liquid Glass Light Color Scheme
private val LiquidGlassLightScheme = lightColorScheme(
    primary = PurpleAccent,
    onPrimary = Color.White,
    primaryContainer = PurpleLight,
    onPrimaryContainer = PurpleDark,
    
    secondary = BlueAccent,
    onSecondary = Color.White,
    secondaryContainer = LightSurfaceVariant,
    onSecondaryContainer = LightTextPrimary,
    
    tertiary = StatusOrange,
    onTertiary = Color.White,
    
    background = LightBackground,
    onBackground = LightTextPrimary,
    
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    
    error = StatusRed,
    onError = Color.White,
    errorContainer = StatusRedLight,
    onErrorContainer = Color.White,
    
    outline = LightGlassBorder,
    outlineVariant = LightGlassWhite
)

@Composable
fun BBooksTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disable dynamic to use custom schemes
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) LiquidGlassDarkScheme else LiquidGlassLightScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            
            // If light theme, use dark icons
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}