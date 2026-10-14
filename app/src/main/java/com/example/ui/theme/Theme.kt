package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PeachPrimary,
    onPrimary = OnPeachPrimary,
    primaryContainer = PeachPrimaryContainer,
    onPrimaryContainer = OnPeachPrimaryContainer,
    secondary = SageSecondary,
    onSecondary = OnSageSecondary,
    secondaryContainer = SageSecondaryContainer,
    onSecondaryContainer = OnSageSecondaryContainer,
    tertiary = AmberTertiary,
    onTertiary = OnAmberTertiary,
    tertiaryContainer = AmberTertiaryContainer,
    onTertiaryContainer = OnAmberTertiaryContainer,
    background = PitchBlack,
    onBackground = TextHighContrast,
    surface = PitchBlack,
    onSurface = TextHighContrast,
    surfaceVariant = SurfaceDarkContainer,
    onSurfaceVariant = TextMediumContrast,
    surfaceContainerLowest = SurfaceDarkLowest,
    surfaceContainerLow = SurfaceDarkLow,
    surfaceContainer = SurfaceDark,
    surfaceContainerHigh = SurfaceDarkHigh,
    surfaceContainerHighest = SurfaceDarkHighest,
    outline = BorderOutline,
    outlineVariant = BorderOutlineSubtle
)

private val LightColorScheme = lightColorScheme(
    primary = PeachPrimaryLight,
    onPrimary = TextHighContrast,
    primaryContainer = PeachPrimaryContainer,
    onPrimaryContainer = OnPeachPrimaryContainer,
    secondary = SageSecondaryLight,
    onSecondary = TextHighContrast,
    secondaryContainer = SageSecondaryContainer,
    onSecondaryContainer = OnSageSecondaryContainer,
    background = SurfaceLight,
    onBackground = PitchBlack,
    surface = SurfaceLight,
    onSurface = PitchBlack,
    surfaceVariant = SurfaceLightContainer,
    onSurfaceVariant = TextMediumContrast
)

@Composable
fun SonoraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    amoledDark: Boolean = true,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val dynamic = if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            if (darkTheme && amoledDark) {
                dynamic.copy(
                    background = PitchBlack,
                    surface = PitchBlack,
                    surfaceContainerLowest = SurfaceDarkLowest,
                    surfaceContainerLow = SurfaceDarkLow,
                    surfaceContainer = SurfaceDark,
                    surfaceContainerHigh = SurfaceDarkHigh,
                    surfaceContainerHighest = SurfaceDarkHighest
                )
            } else {
                dynamic
            }
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

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    SonoraTheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor,
        content = content
    )
}
