package com.example.composetrainer.ui.screens.component

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.composetrainer.R
import com.example.composetrainer.ui.screens.setting.CurrencyPreferences

@Composable
fun CurrencyIcon(
    modifier: Modifier = Modifier,
    contentDescription: String? = "Currency",
    tint : Color? = null
) {
    val context = LocalContext.current
    val selectedCurrency = remember { CurrencyPreferences.getCurrency(context) }

    val iconId = when (selectedCurrency) {
        "Rial" -> R.drawable.currencyrial
        "Toman" -> R.drawable.toman
        else -> R.drawable.currencyrial // Default fallback
    }

    Icon(
        painter = painterResource(id = iconId),
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint ?: Color.Unspecified
    )
}