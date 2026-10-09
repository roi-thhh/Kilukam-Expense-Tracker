package com.example.kilukkam.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val SunnyFintechColorScheme = lightColorScheme(
    primary = BrandYellowPrimary,
    onPrimary = TextOnYellow,
    primaryContainer = BrandYellowSoft,
    onPrimaryContainer = TextDark,
    secondary = BrandYellowWarm,
    onSecondary = TextDark,
    secondaryContainer = BackgroundMuted,
    onSecondaryContainer = TextDark,
    tertiary = AccentSky,
    onTertiary = TextDark,
    background = BackgroundCanvas,
    onBackground = TextDark,
    surface = SurfaceWhite,
    onSurface = TextDark,
    surfaceVariant = BackgroundMuted,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    outlineVariant = BorderWarm,
    error = AccentExpense,
    onError = SurfaceWhite
)

@Composable
fun KilukkamTheme(
    darkTheme: Boolean = false, // Default to Sunny Fintech light palette
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = SunnyFintechColorScheme,
        typography = Typography,
        content = content
    )
}
