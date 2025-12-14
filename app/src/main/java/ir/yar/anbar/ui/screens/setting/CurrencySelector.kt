package ir.yar.anbar.ui.screens.setting

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import ir.yar.anbar.R
import ir.yar.anbar.ui.theme.BKoodak
import ir.yar.anbar.ui.theme.Beirut_Medium
import ir.yar.anbar.utils.dimen
import ir.yar.anbar.utils.dimenTextSize
import ir.yar.anbar.utils.str

@Composable
fun CurrencySelector() {
    val context = LocalContext.current
    var selectedCurrency by remember {
        mutableStateOf(CurrencyPreferences.getCurrency(context))
    }


    LaunchedEffect(selectedCurrency) {
        CurrencyPreferences.saveCurrency(context, selectedCurrency)
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth().padding(horizontal = dimen(R.dimen.space_2), vertical = dimen(R.dimen.space_2)),
        shape = RoundedCornerShape(dimen(R.dimen.radius_md)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimen(R.dimen.space_6),
                    vertical = dimen(R.dimen.space_4)
                ),
            verticalAlignment = Alignment.CenterVertically
        )  {

            Column(modifier = Modifier.weight(3f)) {
                Text(
                    text = str(R.string.select_currency),
                    fontFamily = Beirut_Medium,
                    fontSize = dimenTextSize(R.dimen.text_size_lg),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = if (selectedCurrency == "Rial") str(R.string.rial) else str(R.string.toman),
                    fontSize = dimenTextSize(R.dimen.text_size_md),
                    fontFamily = BKoodak,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            CurrencyOption(
                modifier = Modifier.weight(1f),
                iconId = R.drawable.currencyrial,
                isSelected = selectedCurrency == "Rial",
                onClick = { selectedCurrency = "Rial" }
            )

            CurrencyOption(
                modifier = Modifier.weight(1f),
                iconId = R.drawable.toman,
                isSelected = selectedCurrency == "Toman",
                onClick = { selectedCurrency = "Toman" }
            )
        }

    }

}

@Composable
fun CurrencyOption(
    modifier: Modifier = Modifier,
    iconId: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        label = "scale"
    )

    Column(
        modifier = modifier.padding(horizontal = dimen(R.dimen.space_2)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Box(
            modifier = Modifier
                .size(dimen(R.dimen.size_lg))
                .scale(scale)
                .border(
                    width = 2.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    shape = RoundedCornerShape(dimen(R.dimen.radius_sm))
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {

                Icon(
                    painter = painterResource(id = iconId),
                    contentDescription = "Price",
                    modifier = Modifier.size(dimen(R.dimen.size_sm)),
                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
        }

        Spacer(modifier = Modifier.height(dimen(R.dimen.space_1)))

        Icon(
                painter = painterResource(id = R.drawable.check_24px),
                contentDescription = "check",
                modifier = Modifier.size(dimen(R.dimen.size_xs)),
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
            )

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