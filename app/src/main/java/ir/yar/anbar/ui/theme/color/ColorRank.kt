package ir.yar.anbar.ui.theme.color

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

object ColorRank {

    val goldLight = Color(0xFFFFD700)
    val goldDark = Color(0xFFFFC107)
    val goldBgLight = Color(0xFFFFF8E1)
    val goldBgDark = Color(0xFF4E3B00)

    val silverLight = Color(0xFFC0C0C0)
    val silverDark = Color(0xFFB0BEC5)
    val silverBgLight = Color(0xFFF5F5F5)
    val silverBgDark = Color(0xFF2E2E2E)

    val bronzeLight = Color(0xFFCD7F32)
    val bronzeDark = Color(0xFFD2691E)
    val bronzeBgLight = Color(0xFFFFF3E0)
    val bronzeBgDark = Color(0xFF3E2723)

}

val ColorScheme.gold: Color
    @Composable
    get() = if (this.background.luminance() < 0.5) {
        ColorRank.goldDark
    } else {
        ColorRank.goldLight
    }

val ColorScheme.goldBg: Color
    @Composable
    get() = if (this.background.luminance() < 0.5) {
        ColorRank.goldBgDark
    } else {
        ColorRank.goldBgLight
    }

val ColorScheme.silver: Color
    @Composable
    get() = if (this.background.luminance() < 0.5) {
        ColorRank.silverDark
    } else {
        ColorRank.silverLight
    }

val ColorScheme.silverBg: Color
    @Composable
    get() = if (this.background.luminance() < 0.5) {
        ColorRank.silverBgDark
    } else {
        ColorRank.silverBgLight
    }

val ColorScheme.bronze: Color
    @Composable
    get() = if (this.background.luminance() < 0.5) {
        ColorRank.bronzeDark
    } else {
        ColorRank.bronzeLight
    }

val ColorScheme.bronzeBg: Color
    @Composable
    get() = if (this.background.luminance() < 0.5) {
        ColorRank.bronzeBgDark
    } else {
        ColorRank.bronzeBgLight
    }