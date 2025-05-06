package com.example.composetrainer.ui.screens.invoice

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

import com.example.composetrainer.R
import com.example.composetrainer.ui.theme.ComposeTrainerTheme
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.dimenTextSize
import com.example.composetrainer.utils.str
import com.example.composetrainer.utils.DateFormatter
import com.example.composetrainer.data.local.entity.InvoiceEntity
import com.example.composetrainer.domain.model.buildInvoiceCode

@Composable
fun InvoiceScreen(
    onComplete: () -> Unit,
    onClose: () -> Unit,
    invoice: InvoiceEntity? = null
) {
    val persianDate = remember { DateFormatter.getHijriShamsiDate() }
    val currentTime = remember { DateFormatter.getCurrentTimeFormatted() }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimen(R.dimen.space_4)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                )
            }
            invoice?.let {
                Text(
                    text = it.buildInvoiceCode(),
                    fontSize = dimenTextSize(R.dimen.text_size_md),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            } ?: Spacer(modifier = Modifier.weight(1f))
            Text(
                text = str(R.string.sale_invoice),
                fontSize = dimenTextSize(R.dimen.text_size_xl),
                textAlign = TextAlign.End,
                modifier = Modifier.padding(end = dimen(R.dimen.space_2))
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimen(R.dimen.space_4),
                    vertical = dimen(R.dimen.space_4)
                )
                .height(IntrinsicSize.Min)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Date",
                        modifier = Modifier.padding(end = dimen(R.dimen.space_1))
                    )
                    Text(text = persianDate)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = dimen(R.dimen.space_2))
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Time",
                        modifier = Modifier.padding(end = dimen(R.dimen.space_1))
                    )
                    Text(text = currentTime)
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(text = str(R.string.buyer))
                    Text(text = str(R.string.unknown))
                }
                Icon(
                    painter = painterResource(id = R.drawable.face_24px),
                    contentDescription = "User",
                    modifier = Modifier
                        .padding(start = dimen(R.dimen.space_2))
                        .fillMaxHeight()
                        .aspectRatio(1f)
                )
            }
        }
    }
}

@Preview(
    name = "Invoice Screen Preview",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun InvoiceScreenPreview() {
    ComposeTrainerTheme {
        InvoiceScreen(
            onComplete = {},
            onClose = {},
            invoice = InvoiceEntity(
                prefix = "INV",
                invoiceDate = "1403-02-16",
                invoiceNumber = 25,
                invoiceType = "S",
                customerCode = "C001"
            )
        )
    }
}