package com.galang.bbooks.ui.theme

import androidx.compose.ui.graphics.Color

// Legacy Colors (kept for compatibility)
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// ========================================
// Dark Theme - Liquid Glass Palette
// Green/Teal Theme
// ========================================

// Background Colors
val DarkBackground = Color(0xFF0A1612)      // Very dark green-tinted
val DarkSurface = Color(0xFF0F1F1A)         // Dark teal-tinted surface
val DarkSurfaceVariant = Color(0xFF1A2E28) // Slightly lighter
val DarkSurfaceElevated = Color(0xFF152420) // Elevated surface

// Teal/Green Gradient Accent Colors
val PurpleAccent = Color(0xFF00BDA5)        // Main teal accent (#00bda5)
val BlueAccent = Color(0xFF00897B)          // Darker teal for gradient
val PurpleLight = Color(0xFF64FFDA)         // Light teal/mint
val PurpleDark = Color(0xFF00695C)          // Deep teal

// Alternative accent names (same colors, semantic names)
val TealAccent = Color(0xFF00BDA5)
val TealDark = Color(0xFF00897B)
val TealLight = Color(0xFF64FFDA)
val GreenAccent = Color(0xFF4CAF50)

// Status Colors
val StatusGreen = Color(0xFF4CAF50)
val StatusGreenLight = Color(0xFF81C784)
val StatusRed = Color(0xFFFF5252)
val StatusRedLight = Color(0xFFFF8A80)
val StatusOrange = Color(0xFFFF9800)
val StatusOrangeLight = Color(0xFFFFB74D)

// Text Colors
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xB3FFFFFF) // 70% white
val TextTertiary = Color(0x80FFFFFF) // 50% white
val TextDisabled = Color(0x4DFFFFFF) // 30% white

// Glass Effect Colors
val GlassWhite = Color(0x1AFFFFFF)     // 10% white - card background
val GlassBorder = Color(0x33FFFFFF)    // 20% white - subtle border
val GlassHighlight = Color(0x26FFFFFF) // 15% white - highlight
val GlassDark = Color(0x1A000000)      // 10% black - shadow

// Accent Gradient (for buttons, highlights)
val GradientStart = PurpleAccent       // Teal #00BDA5
val GradientEnd = BlueAccent           // Dark teal

// ========================================
// Light Theme - Liquid Glass Palette
// ========================================

val LightBackground = Color(0xFFF5F8F7)     // Slightly green-tinted white
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFE8F5F3) // Light teal tint
val LightTextPrimary = Color(0xFF1A1A1A)
val LightTextSecondary = Color(0xFF5A5A5A)
val LightGlassWhite = Color(0x99FFFFFF) // More opaque for light mode
val LightGlassBorder = Color(0x1A000000) // Dark border

// ========================================
// Legacy Pastel Colors (for fallback)
// ========================================
val PastelBlue = Color(0xFFE0F7F3)      // Teal-tinted pastel
val SoftBlue = Color(0xFF26A69A)        // Soft teal
val PastelMint = Color(0xFFB9F6CA)
val SoftCoral = Color(0xFFFF8A65)
val DeepBlue = Color(0xFF00695C)        // Deep teal
val WhiteTranslucent = Color(0xCCFFFFFF)