package com.example.composetrainer.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.composetrainer.R
import com.example.composetrainer.ui.theme.color.Charcoal
import com.example.composetrainer.ui.theme.color.JetBlack
import com.example.composetrainer.ui.theme.color.cultured_white
import com.example.composetrainer.ui.theme.color.green
import com.example.composetrainer.ui.theme.color.md_theme_dark_error
import com.example.composetrainer.ui.theme.color.md_theme_dark_errorContainer
import com.example.composetrainer.ui.theme.color.md_theme_dark_onBackground
import com.example.composetrainer.ui.theme.color.md_theme_dark_onError
import com.example.composetrainer.ui.theme.color.md_theme_dark_onErrorContainer
import com.example.composetrainer.ui.theme.color.md_theme_dark_onPrimary
import com.example.composetrainer.ui.theme.color.md_theme_dark_onPrimaryContainer
import com.example.composetrainer.ui.theme.color.md_theme_dark_onSecondary
import com.example.composetrainer.ui.theme.color.md_theme_dark_onSecondaryContainer
import com.example.composetrainer.ui.theme.color.md_theme_dark_onSurface
import com.example.composetrainer.ui.theme.color.md_theme_dark_onSurfaceVariant
import com.example.composetrainer.ui.theme.color.md_theme_dark_onTertiary
import com.example.composetrainer.ui.theme.color.md_theme_dark_onTertiaryContainer
import com.example.composetrainer.ui.theme.color.md_theme_dark_outline
import com.example.composetrainer.ui.theme.color.md_theme_dark_outlineVariant
import com.example.composetrainer.ui.theme.color.md_theme_dark_primary
import com.example.composetrainer.ui.theme.color.md_theme_dark_primaryContainer
import com.example.composetrainer.ui.theme.color.md_theme_dark_secondary
import com.example.composetrainer.ui.theme.color.md_theme_dark_secondaryContainer
import com.example.composetrainer.ui.theme.color.md_theme_dark_surfaceVariant
import com.example.composetrainer.ui.theme.color.md_theme_dark_tertiary
import com.example.composetrainer.ui.theme.color.md_theme_dark_tertiaryContainer
import com.example.composetrainer.ui.theme.color.md_theme_light_error
import com.example.composetrainer.ui.theme.color.md_theme_light_errorContainer
import com.example.composetrainer.ui.theme.color.md_theme_light_onBackground
import com.example.composetrainer.ui.theme.color.md_theme_light_onError
import com.example.composetrainer.ui.theme.color.md_theme_light_onErrorContainer
import com.example.composetrainer.ui.theme.color.md_theme_light_onPrimary
import com.example.composetrainer.ui.theme.color.md_theme_light_onPrimaryContainer
import com.example.composetrainer.ui.theme.color.md_theme_light_onSecondary
import com.example.composetrainer.ui.theme.color.md_theme_light_onSecondaryContainer
import com.example.composetrainer.ui.theme.color.md_theme_light_onSurface
import com.example.composetrainer.ui.theme.color.md_theme_light_onSurfaceVariant
import com.example.composetrainer.ui.theme.color.md_theme_light_onTertiary
import com.example.composetrainer.ui.theme.color.md_theme_light_onTertiaryContainer
import com.example.composetrainer.ui.theme.color.md_theme_light_outline
import com.example.composetrainer.ui.theme.color.md_theme_light_outlineVariant
import com.example.composetrainer.ui.theme.color.md_theme_light_primary
import com.example.composetrainer.ui.theme.color.md_theme_light_primaryContainer
import com.example.composetrainer.ui.theme.color.md_theme_light_secondary
import com.example.composetrainer.ui.theme.color.md_theme_light_secondaryContainer
import com.example.composetrainer.ui.theme.color.md_theme_light_surface
import com.example.composetrainer.ui.theme.color.md_theme_light_surfaceVariant
import com.example.composetrainer.ui.theme.color.md_theme_light_tertiaryContainer

// Remove all md_theme_* color val definitions here. Only Color.kt defines palette colors.

private val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    onError = md_theme_dark_onError,
    errorContainer = md_theme_dark_errorContainer,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = JetBlack,
    onBackground = md_theme_dark_onBackground,
    surface = Charcoal,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    outlineVariant = md_theme_dark_outlineVariant
)

private val LightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = green,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    onError = md_theme_light_onError,
    errorContainer = md_theme_light_errorContainer,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = cultured_white,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    outlineVariant = md_theme_light_outlineVariant

)

@Composable
fun ComposeTrainerTheme(
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

    val myFontFamily = FontFamily(
        Font(R.font.zar_bold, FontWeight.Normal)
    )

    val customTypography = Typography(
        bodyLarge = TextStyle(
            fontFamily = myFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        )
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = customTypography,
        content = content
    )
}