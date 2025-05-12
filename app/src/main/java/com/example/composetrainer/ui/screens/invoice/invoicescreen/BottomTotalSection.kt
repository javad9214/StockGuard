package com.example.composetrainer.ui.screens.invoice.invoicescreen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composetrainer.R
import com.example.composetrainer.ui.theme.ComposeTrainerTheme
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.str

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
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
            Text(
                text = str(R.string.total),
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "$totalPrice",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = dimen(R.dimen.space_2))
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onSubmit,
                modifier = Modifier.padding(start = dimen(R.dimen.space_2)),
                enabled = hasItems && !isLoading
            ) {
                Text(
                    text = str(R.string.submit),
                    style = MaterialTheme.typography.titleMedium
                )
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