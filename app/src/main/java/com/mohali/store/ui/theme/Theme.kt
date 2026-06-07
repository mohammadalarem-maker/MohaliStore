package com.mohali.store.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ============================
// COLORS
// ============================
object MohaliColors {
    // Primary Gradient Colors
    val Primary = Color(0xFF1A1A2E)       // Deep navy
    val PrimaryVariant = Color(0xFF16213E) // Darker navy
    val Secondary = Color(0xFF0F3460)      // Royal blue
    val Accent = Color(0xFFE94560)         // Vibrant red/pink
    val AccentGold = Color(0xFFFFD700)     // Gold
    val AccentTeal = Color(0xFF00B4D8)     // Teal

    // Surface Colors
    val Surface = Color(0xFF1E1E2E)
    val SurfaceVariant = Color(0xFF252535)
    val Card = Color(0xFF2A2A3E)
    val CardBorder = Color(0xFF3A3A5A)

    // Text Colors
    val OnPrimary = Color(0xFFFFFFFF)
    val OnSurface = Color(0xFFE8E8F0)
    val OnSurfaceVariant = Color(0xFFB0B0C8)
    val Muted = Color(0xFF7070A0)

    // Status Colors
    val Success = Color(0xFF00C896)
    val Warning = Color(0xFFFFB347)
    val Error = Color(0xFFFF6B6B)
    val Info = Color(0xFF64B5F6)

    // Category Colors
    val PhonesColor = Color(0xFF4FC3F7)
    val AccessoriesColor = Color(0xFFFFD54F)
    val ElectronicsColor = Color(0xFF81C784)
    val CosmeticsColor = Color(0xFFF48FB1)
    val BeautyToolsColor = Color(0xFFCE93D8)
    val ChargersColor = Color(0xFFFFCC02)

    // Chart Colors
    val ChartColors = listOf(
        Color(0xFF00B4D8), Color(0xFFE94560), Color(0xFF00C896),
        Color(0xFFFFD700), Color(0xFFCE93D8), Color(0xFFFF6B6B)
    )

    // Gradient
    val GradientStart = Color(0xFF1A1A2E)
    val GradientMid = Color(0xFF16213E)
    val GradientEnd = Color(0xFF0F3460)

    // Login Screen
    val LoginBg1 = Color(0xFF0A0A1A)
    val LoginBg2 = Color(0xFF1A1A3E)
    val LoginCard = Color(0x881A1A3E)
}

// ============================
// DARK COLOR SCHEME
// ============================
private val DarkColorScheme = darkColorScheme(
    primary = MohaliColors.Accent,
    onPrimary = MohaliColors.OnPrimary,
    primaryContainer = MohaliColors.Secondary,
    onPrimaryContainer = MohaliColors.OnPrimary,
    secondary = MohaliColors.AccentTeal,
    onSecondary = MohaliColors.OnPrimary,
    secondaryContainer = Color(0xFF1A3A5C),
    onSecondaryContainer = MohaliColors.OnSurface,
    background = MohaliColors.Primary,
    onBackground = MohaliColors.OnSurface,
    surface = MohaliColors.Surface,
    onSurface = MohaliColors.OnSurface,
    surfaceVariant = MohaliColors.SurfaceVariant,
    onSurfaceVariant = MohaliColors.OnSurfaceVariant,
    error = MohaliColors.Error,
    onError = MohaliColors.OnPrimary,
    outline = MohaliColors.CardBorder
)

// ============================
// TYPOGRAPHY
// ============================
val MohaliTypography = Typography(
    displayLarge = TextStyle(
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.5).sp
    ),
    displayMedium = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold
    ),
    headlineLarge = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    ),
    headlineMedium = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold
    ),
    headlineSmall = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold
    ),
    titleLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold
    ),
    titleMedium = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal
    ),
    bodySmall = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal
    ),
    labelLarge = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium
    ),
    labelMedium = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
    ),
    labelSmall = TextStyle(
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.5.sp
    )
)

// ============================
// THEME
// ============================
@Composable
fun MohaliStoreTheme(
    darkTheme: Boolean = true, // Always dark by default for premium look
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = MohaliTypography,
        content = content
    )
}
