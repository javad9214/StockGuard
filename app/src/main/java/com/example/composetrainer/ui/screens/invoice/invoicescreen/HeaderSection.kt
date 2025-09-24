package com.example.composetrainer.ui.screens.invoice.invoicescreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.composetrainer.R
import com.example.composetrainer.ui.theme.BKoodak
import com.example.composetrainer.ui.theme.BLotus
import com.example.composetrainer.ui.theme.BMitra
import com.example.composetrainer.ui.theme.BNazanin
import com.example.composetrainer.ui.theme.Beirut_Medium
import com.example.composetrainer.ui.theme.ComposeTrainerTheme
import com.example.composetrainer.ui.theme.smoke_white
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.dimenTextSize
import com.example.composetrainer.utils.str

@Composable
fun HeaderSection(
    invoiceNumber: String,
    persianDate: String,
    currentTime: String,
    onAddProductClick: () -> Unit,
    onClose: () -> Unit,
    onScanBarcodeClick: () -> Unit = {}
) {

    var isSaleInvoice by remember { mutableStateOf(true) }


    CompositionLocalProvider(LocalLayoutDirection.provides(LayoutDirection.Ltr)) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(
                bottomStart = dimen(R.dimen.radius_xl),
                bottomEnd = dimen(R.dimen.radius_xl)
            ),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {

            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimen(R.dimen.space_1), end = dimen(R.dimen.space_2)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                        )
                    }

                    Text(
                        text = invoiceNumber,
                        fontFamily = BMitra,
                        fontWeight = FontWeight.Bold,
                        fontSize = dimenTextSize(R.dimen.text_size_md),
                        modifier = Modifier.padding(start = dimen(R.dimen.space_2))
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    ElevatedCard(
                        modifier = Modifier.clickable(
                            indication = ripple(
                                color = MaterialTheme.colorScheme.primary,
                                bounded = true
                            ),
                            interactionSource = remember { MutableInteractionSource() }
                        ) { isSaleInvoice = !isSaleInvoice },
                        shape = RoundedCornerShape(dimen(R.dimen.radius_md)),
                        colors = CardDefaults.cardColors(containerColor = smoke_white),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(
                                horizontal = dimen(R.dimen.space_2),
                                vertical = dimen(R.dimen.space_1)
                            ),
                        ) {
                            if (isSaleInvoice) {
                                Icon(
                                    modifier = Modifier
                                        .size(dimen(R.dimen.size_xs))
                                        .rotate(90f),
                                    painter = painterResource(id = R.drawable.output_circle_24px),
                                    contentDescription = "Arrow Down"
                                )

                                Text(
                                    modifier = Modifier.padding(horizontal = dimen(R.dimen.space_2)),
                                    text = str(R.string.sale_invoice),
                                    fontSize = dimenTextSize(R.dimen.text_size_xl),
                                    textAlign = TextAlign.Center,
                                    fontFamily = Beirut_Medium
                                )
                            } else {
                                Icon(
                                    modifier = Modifier
                                        .size(dimen(R.dimen.size_xs))
                                        .rotate(90f),
                                    painter = painterResource(id = R.drawable.input_circle_24px),
                                    contentDescription = "Arrow Down"
                                )

                                Text(
                                    modifier = Modifier.padding(horizontal = dimen(R.dimen.space_2)),
                                    text = str(R.string.purchase_invoice),
                                    fontSize = dimenTextSize(R.dimen.text_size_xl),
                                    textAlign = TextAlign.Center,
                                    fontFamily = Beirut_Medium
                                )
                            }

                        }

                    }

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
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.calendar_today_24px),
                                contentDescription = "Date",
                                modifier = Modifier.padding(end = dimen(R.dimen.space_1))
                            )
                            Text(
                                text = persianDate,
                                fontFamily = BMitra,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(vertical = dimen(R.dimen.space_1))
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(top = dimen(R.dimen.space_2))
                                .height(32.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.schedule_24px),
                                contentDescription = "Time",
                                modifier = Modifier.padding(end = dimen(R.dimen.space_1))
                            )
                            Text(
                                text = currentTime,
                                fontFamily = BMitra,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(vertical = dimen(R.dimen.space_1))
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            Text(
                                text = if (isSaleInvoice) str(R.string.buyer) else str(R.string.seller),
                                fontFamily = BKoodak,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = str(R.string.unknown),
                                fontFamily = BKoodak,
                                fontWeight = FontWeight.Bold
                            )
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

                // Add product button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = dimen(R.dimen.space_4),
                            vertical = dimen(R.dimen.space_2)
                        ),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Add product button
                    OutlinedButton(
                        onClick = onAddProductClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(dimen(R.dimen.radius_md))
                    ) {
                        Text(
                            text = str(R.string.choose_from_list),
                            fontSize = dimenTextSize(R.dimen.text_size_xl),
                            textAlign = TextAlign.Center,
                            fontFamily = Beirut_Medium

                        )
                    }

                    // Scan barcode button
                    OutlinedButton(
                        onClick = onScanBarcodeClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(dimen(R.dimen.radius_md))
                    ) {
                        Text(
                            modifier = Modifier.padding(end = dimen(R.dimen.space_1)),
                            text = str(R.string.scan_barcode),
                            fontSize = dimenTextSize(R.dimen.text_size_xl),
                            textAlign = TextAlign.Center,
                            fontFamily = Beirut_Medium
                        )

                        Icon(
                            painter = painterResource(id = R.drawable.barcode_scanner_24px),
                            contentDescription = "Scan Barcode",
                        )
                    }

                }
            }
        }
    }
}

@Preview(
    name = "Header Section Preview",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5
)
@Composable
fun HeaderSectionPreview() {
    ComposeTrainerTheme {
        HeaderSection(
            invoiceNumber = "1234",
            persianDate = "1402/12/25",
            currentTime = "14:30",
            onAddProductClick = { },
            onClose = { }
        )
    }
}

@Preview(
    name = "Header Section Preview - No Invoice Number",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5
)
@Composable
fun HeaderSectionNoInvoiceNumberPreview() {
    ComposeTrainerTheme {
        HeaderSection(
            invoiceNumber = "12324",
            persianDate = "1402/12/25",
            currentTime = "14:30",
            onAddProductClick = { },
            onClose = { }
        )
    }
}