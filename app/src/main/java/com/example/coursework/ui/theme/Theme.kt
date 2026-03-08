package com.example.coursework.ui.theme

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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

private val DarkColorScheme = darkColorScheme(
    primary = Green80,
    onPrimary = Color(0xFF003919),
    primaryContainer = Green20,
    onPrimaryContainer = GreenContainer,
    secondary = Color(0xFFB4CCB9),
    surface = SurfaceDark,
    onSurface = Color(0xFFE1E3E0),
    surfaceVariant = Color(0xFF2D3130),
    onSurfaceVariant = Color(0xFFC1C9C2),
)

private val LightColorScheme = lightColorScheme(
    primary = Green40,
    onPrimary = Color.White,
    primaryContainer = GreenContainer,
    onPrimaryContainer = OnGreenContainer,
    secondary = Color(0xFF4F6353),
    surface = Surface,
    onSurface = Color(0xFF1A1C1B),
    surfaceVariant = Color(0xFFE8EDE9),
    onSurfaceVariant = Color(0xFF414942),
)

private fun scaleTypography(base: androidx.compose.material3.Typography, scale: Float): androidx.compose.material3.Typography {
    fun TextStyle.scaled() = copy(
        fontSize = (fontSize.value * scale).sp,
        lineHeight = (lineHeight.value * scale).sp
    )
    return androidx.compose.material3.Typography(
        headlineLarge = base.headlineLarge.scaled(),
        headlineMedium = base.headlineMedium.scaled(),
        headlineSmall = base.headlineSmall.scaled(),
        titleLarge = base.titleLarge.scaled(),
        titleMedium = base.titleMedium.scaled(),
        titleSmall = base.titleSmall.scaled(),
        bodyLarge = base.bodyLarge.scaled(),
        bodyMedium = base.bodyMedium.scaled(),
        bodySmall = base.bodySmall.scaled(),
        labelLarge = base.labelLarge.scaled(),
        labelMedium = base.labelMedium.scaled(),
        labelSmall = base.labelSmall.scaled(),
        displayLarge = base.displayLarge.scaled(),
        displayMedium = base.displayMedium.scaled(),
        displaySmall = base.displaySmall.scaled()
    )
}

@Composable
fun CourseworkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    fontScale: Float = 1.0f,
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

    val scaledTypography = if (fontScale != 1.0f) scaleTypography(Typography, fontScale) else Typography

    MaterialTheme(
        colorScheme = colorScheme,
        typography = scaledTypography,
        content = content
    )
}
