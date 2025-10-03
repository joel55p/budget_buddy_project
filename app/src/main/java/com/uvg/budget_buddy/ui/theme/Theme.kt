package com.uvg.budget_buddy.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary           = GreenPrimary,
    onPrimary         = PureWhite,
    secondary         = GreenPrimaryDark,
    onSecondary       = PureWhite,
    background        = GrayLight1,
    surface           = PureWhite,
    primaryContainer  = GreenPrimaryLight,
    surfaceVariant    = GrayLight2,
    error             = SoftRed,
)

private val DarkColors = darkColorScheme(
    primary     = GreenPrimary,
    onPrimary   = PureWhite,
    secondary   = GreenPrimaryDark,
    onSecondary = PureWhite,
    error       = SoftRed,
)

@Composable
fun Budget_buddyTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography  = Typography,
        content     = content
    )
}