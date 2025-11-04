package com.example.composetrainer.ui.screens.setting

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import com.example.composetrainer.R
import com.example.composetrainer.ui.theme.MRTPoster
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.dimenTextSize
import com.example.composetrainer.utils.str

@Composable
fun CurrencySelector() {
    val context = LocalContext.current
    var selectedCurrency by remember {
        mutableStateOf(CurrencyPreferences.getCurrency(context))
    }


    LaunchedEffect(selectedCurrency) {
        CurrencyPreferences.saveCurrency(context, selectedCurrency)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            str(R.string.select_currency),
            fontFamily = MRTPoster,
            fontSize = dimenTextSize(R.dimen.text_size_lg),
            modifier = Modifier.padding(start = dimen(R.dimen.space_4)),
        )

        CurrencyOption(
            iconId = R.drawable.currencyrial,
            isSelected = selectedCurrency == "Rial",
            onClick = { selectedCurrency = "Rial" }
        )

        CurrencyOption(
            iconId = R.drawable.toman,
            isSelected = selectedCurrency == "Toman",
            onClick = { selectedCurrency = "Toman" }
        )
    }


}

@Composable
fun CurrencyOption(
    iconId: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(dimen(R.dimen.size_3xl))
            .scale(scale)
            .border(
                width = 2.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(dimen(R.dimen.radius_sm))
            )
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(dimen(R.dimen.radius_sm))
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Icon(
                painter = painterResource(id = iconId),
                contentDescription = "Price",
                modifier = Modifier.size(dimen(R.dimen.size_md)),
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
            )

            if (isSelected) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

object CurrencyPreferences {
    private const val PREF_NAME = "currency_settings"
    private const val KEY_SELECTED_CURRENCY = "selected_currency"
    private const val DEFAULT_CURRENCY = "Rial"

    fun saveCurrency(context: Context, currency: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit { putString(KEY_SELECTED_CURRENCY, currency) }
    }

    fun getCurrency(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_SELECTED_CURRENCY, DEFAULT_CURRENCY) ?: DEFAULT_CURRENCY
    }
}