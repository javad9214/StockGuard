package com.example.composetrainer.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.composetrainer.R
import com.example.composetrainer.ui.theme.BNazanin
import com.example.composetrainer.ui.theme.Beirut_Medium
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.dimenTextSize
import com.example.composetrainer.utils.str

@Composable
fun TotalsItem(
    modifier: Modifier = Modifier,
) {

    ElevatedCard(
        modifier = modifier
            .padding(dimen(R.dimen.space_4))
            .wrapContentWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(dimen(R.dimen.radius_lg)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(dimen(R.dimen.space_2))) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(dimen(R.dimen.space_2)),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = str(R.string.total_sales),
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = Beirut_Medium,
                    fontSize = dimenTextSize(R.dimen.text_size_lg)
                )

                Row( verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {

                    Text(
                        text = str(R.string.total_sales),
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = BNazanin,
                        fontSize = dimenTextSize(R.dimen.text_size_lg)
                    )

                    Spacer(modifier = Modifier.padding(dimen(R.dimen.space_1)))

                    Icon(
                        modifier = Modifier.size(dimen(R.dimen.size_sm)).padding(end = dimen(R.dimen.space_1)),
                        painter = painterResource(id = R.drawable.toman),
                        contentDescription = "down",
                    )
                }

            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant)

            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(dimen(R.dimen.space_2)),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = str(R.string.total_profit),
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = Beirut_Medium,
                    fontSize = dimenTextSize(R.dimen.text_size_lg)
                )

                Row ( verticalAlignment = Alignment.CenterVertically) {

                    Text(
                        text = str(R.string.today),
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = Beirut_Medium,
                        fontSize = dimenTextSize(R.dimen.text_size_lg)
                    )

                    Icon(
                        modifier = Modifier.size(dimen(R.dimen.size_sm)).padding(end = dimen(R.dimen.space_1)),
                        painter = painterResource(id = R.drawable.toman),
                        contentDescription = "down",
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant)


            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(dimen(R.dimen.space_2)),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = str(R.string.total_invoice_registered),
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = Beirut_Medium,
                    fontSize = dimenTextSize(R.dimen.text_size_lg)
                )

                Icon(
                    modifier = Modifier.padding(end = dimen(R.dimen.space_1)),
                    painter = painterResource(id = R.drawable.keyboard_arrow_down_24px),
                    contentDescription = "down",
                )
            }

        }
    }

}