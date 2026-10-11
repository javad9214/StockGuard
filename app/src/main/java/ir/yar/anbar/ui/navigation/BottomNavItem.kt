package ir.yar.anbar.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    @StringRes val titleRes: Int,
    val route: String,
    val icon: ImageVector? = null
)
