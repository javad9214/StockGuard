package com.example.composetrainer.ui.screens.invoice.invoicescreen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composetrainer.R
import com.example.composetrainer.ui.theme.BHoma
import com.example.composetrainer.ui.theme.ComposeTrainerTheme
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.str
import com.example.composetrainer.utils.price.PriceValidator
import com.example.composetrainer.utils.dimenTextSize

@Composable
fun BottomTotalSection(
    totalPrice: Long,
    isLoading: Boolean,
    hasItems: Boolean,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(
            topStart = dimen(R.dimen.radius_xl),
            topEnd = dimen(R.dimen.radius_xl)
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp,
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimen(R.dimen.space_4),
                    vertical = dimen(R.dimen.space_4)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.toman),
                contentDescription = "Date",
                modifier = Modifier
                    .size(dimen(R.dimen.size_lg))
            )

            Text(
                text = PriceValidator.formatPrice(totalPrice.toString()),
                style = MaterialTheme.typography.titleMedium,
                fontFamily = BHoma,
                fontWeight = FontWeight.Bold,
                fontSize = dimenTextSize(R.dimen.text_size_4xl),
                modifier = Modifier.padding(start = dimen(R.dimen.space_2))
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onSubmit,
                modifier = Modifier.padding(start = dimen(R.dimen.space_2)),
                enabled = hasItems && !isLoading,
                shape = RoundedCornerShape(dimen(R.dimen.radius_md))
            ) {
                Text(
                    text = str(R.string.finalize_factor),
                    fontFamily = BHoma,
                    fontSize = dimenTextSize(R.dimen.text_size_xxl),
                    modifier = Modifier.padding(
                        vertical = dimen(R.dimen.space_2), horizontal = dimen(R.dimen.space_4)))
            }
        }
    }
}

@Preview(
    name = "Bottom Total Section Preview",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5
)
@Composable
fun BottomTotalSectionPreview() {
    ComposeTrainerTheme {
        BottomTotalSection(
            totalPrice = 125000,
            isLoading = false,
            hasItems = true,
            onSubmit = { }
        )
    }
}

@Preview(
    name = "Bottom Total Section Preview - No Items",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5
)
@Composable
fun BottomTotalSectionNoItemsPreview() {
    ComposeTrainerTheme {
        BottomTotalSection(
            totalPrice = 0,
            isLoading = false,
            hasItems = false,
            onSubmit = { }
        )
    }
}

@Preview(
    name = "Bottom Total Section Preview - Loading",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5
)
@Composable
fun BottomTotalSectionLoadingPreview() {
    ComposeTrainerTheme {
        BottomTotalSection(
            totalPrice = 125000,
            isLoading = true,
            hasItems = true,
            onSubmit = { }
        )
    }
}