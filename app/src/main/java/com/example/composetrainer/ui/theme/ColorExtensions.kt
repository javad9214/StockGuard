package com.example.composetrainer.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

object CustomColors {

    val successLight = Color(0xFF2E7D32)
    val successDark = Color(0xFF4CAF50)

    val warningLight = Color(0xFFF57C00)
    val warningDark = Color(0xFFFFA726)

    val errorLight = Color(0xFFD32F2F)
    val errorDark = Color(0xFFEF5350)

    val infoLight = Color(0xFF1976D2)
    val infoDark = Color(0xFF42A5F5)

    val costPriceLight = Color(0xFFFF6B6B)
    val costPriceDark = Color(0xFFFFAB91)

    val salePriceLight = Color(0xFF4CAF50)
    val salePriceDark = Color(0xFF81C784)


}



// Extension properties
val ColorScheme.success: Color
    @Composable
    get() = if (this.background == Color.Black ||
        this.background.luminance() < 0.5
    ) {
        CustomColors.successDark
    } else {
        CustomColors.successLight
    }

val ColorScheme.warning: Color
    @Composable
    get() = if (this.background.luminance() < 0.5) {
        CustomColors.warningDark
    } else {
        CustomColors.warningLight
    }

val ColorScheme.customError: Color
    @Composable
    get() = if (this.background.luminance() < 0.5) {
        CustomColors.errorDark
    } else {
        CustomColors.errorLight
    }

val ColorScheme.info: Color
    @Composable
    get() = if (this.background.luminance() < 0.5) {
        CustomColors.infoDark
    } else {
        CustomColors.infoLight
    }

val ColorScheme.costPrice: Color
    @Composable
    get() = if (this.background.luminance() < 0.5) {
        CustomColors.costPriceDark
    } else {
        CustomColors.costPriceLight
    }

val ColorScheme.salePrice: Color
    @Composable
    get() = if (this.background.luminance() < 0.5) {
        CustomColors.salePriceLight
    } else {
        CustomColors.salePriceLight
    }