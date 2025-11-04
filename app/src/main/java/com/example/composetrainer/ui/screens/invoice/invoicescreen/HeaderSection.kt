package com.example.composetrainer.ui.screens.invoice.invoicescreen

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.composetrainer.R
import com.example.composetrainer.domain.model.InvoiceType
import com.example.composetrainer.ui.theme.BKoodak
import com.example.composetrainer.ui.theme.Beirut_Medium
import com.example.composetrainer.ui.theme.ComposeTrainerTheme
import com.example.composetrainer.ui.theme.costPrice
import com.example.composetrainer.ui.theme.costPriceBg
import com.example.composetrainer.ui.theme.salePrice
import com.example.composetrainer.ui.theme.salePriceBg
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.dimenTextSize
import com.example.composetrainer.utils.str

@Composable
fun HeaderSection(
    invoiceNumber: String,
    persianDate: String,
    currentTime: String,
    onClose: () -> Unit,
    onInvoiceTypeChange: (InvoiceType) -> Unit = {}
) {

    var isSaleInvoice by remember { mutableStateOf(true) }

    CompositionLocalProvider(LocalLayoutDirection.provides(LayoutDirection.Ltr)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Top Header Section
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(
                    bottomStart = dimen(R.dimen.radius_xl),
                    bottomEnd = dimen(R.dimen.radius_xl)
                ),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = dimen(R.dimen.space_2),
                            bottom = dimen(R.dimen.space_2),
                            end = dimen(R.dimen.space_2),
                            start = dimen(R.dimen.space_2)
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Close button
                    Box(
                        modifier = Modifier
                            .padding(dimen(R.dimen.space_2))
                            .size(dimen(R.dimen.size_lg))
                            .clip(CircleShape)
                            .background(Color.Gray.copy(alpha = 0.08f))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = LocalIndication.current,
                                onClick = onClose
                            )
                            .padding(dimen(R.dimen.space_2))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurface ,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    Text(
                        text = "#",
                        fontFamily = BKoodak,
                        fontWeight = FontWeight.Bold,
                        fontSize = dimenTextSize(R.dimen.text_size_md),
                        modifier = Modifier.padding(start = dimen(R.dimen.space_2))
                    )

                    Text(
                        text = invoiceNumber,
                        fontFamily = BKoodak,
                        fontWeight = FontWeight.Bold,
                        fontSize = dimenTextSize(R.dimen.text_size_md),
                        modifier = Modifier.padding(start = dimen(R.dimen.space_1))
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    ElevatedCard(
                        modifier = Modifier
                            .clip(RoundedCornerShape(dimen(R.dimen.radius_md)))
                            .clickable(
                                indication = ripple(
                                    color = if (isSaleInvoice) MaterialTheme.colorScheme.salePrice
                                    else MaterialTheme.colorScheme.costPrice,
                                    bounded = true
                                ),
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                isSaleInvoice = !isSaleInvoice
                                if (isSaleInvoice) onInvoiceTypeChange(InvoiceType.SALE)
                                else onInvoiceTypeChange(InvoiceType.PURCHASE)
                            },
                        shape = RoundedCornerShape(dimen(R.dimen.radius_md)),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSaleInvoice) MaterialTheme.colorScheme.salePriceBg
                            else MaterialTheme.colorScheme.costPriceBg
                        ),
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
                                    contentDescription = "Sale Icon ",
                                    tint = MaterialTheme.colorScheme.salePrice
                                )

                                Text(
                                    modifier = Modifier.padding(horizontal = dimen(R.dimen.space_2)),
                                    text = str(R.string.sale_invoice),
                                    fontSize = dimenTextSize(R.dimen.text_size_xl),
                                    textAlign = TextAlign.Center,
                                    fontFamily = Beirut_Medium,
                                    color = MaterialTheme.colorScheme.salePrice
                                )
                            } else {
                                Icon(
                                    modifier = Modifier
                                        .size(dimen(R.dimen.size_xs))
                                        .rotate(90f),
                                    painter = painterResource(id = R.drawable.input_circle_24px),
                                    contentDescription = "Buy Icon",
                                    tint = MaterialTheme.colorScheme.costPrice
                                )

                                Text(
                                    modifier = Modifier.padding(horizontal = dimen(R.dimen.space_2)),
                                    text = str(R.string.purchase_invoice),
                                    fontSize = dimenTextSize(R.dimen.text_size_xl),
                                    textAlign = TextAlign.Center,
                                    fontFamily = Beirut_Medium,
                                    color = MaterialTheme.colorScheme.costPrice
                                )
                            }

                        }

                    }

                }
            }

            Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))

            // Date and Time Card
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimen(R.dimen.space_2)),
                shape = RoundedCornerShape(dimen(R.dimen.radius_md)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Row(
                    modifier = Modifier
                        .padding(
                            vertical = dimen(R.dimen.space_3),
                            horizontal = dimen(R.dimen.space_4)
                        )
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Row(verticalAlignment = Alignment.CenterVertically){
                        Icon(
                            painter = painterResource(id = R.drawable.schedule_24px),
                            contentDescription = "Time",
                            modifier = Modifier
                                .padding(end = dimen(R.dimen.space_2))
                                .size(dimen(R.dimen.size_sm))
                                .alignByBaseline()
                        )

                        Text(
                            text = currentTime,
                            fontFamily = BKoodak,
                            fontWeight = FontWeight.Bold,
                            fontSize = dimenTextSize(R.dimen.text_size_lg),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }


                    Row{
                        Text(
                            text = persianDate,
                            fontFamily = BKoodak,
                            fontWeight = FontWeight.Bold,
                            fontSize = dimenTextSize(R.dimen.text_size_lg),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.calendar_today_24px),
                            contentDescription = "Date",
                            modifier = Modifier
                                .padding(start = dimen(R.dimen.space_2))
                                .size(dimen(R.dimen.size_sm))
                        )
                    }

                }


            }

            Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))

            // User info card
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimen(R.dimen.space_2)),
                shape = RoundedCornerShape(dimen(R.dimen.radius_md)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = dimen(R.dimen.space_3),
                            horizontal = dimen(R.dimen.space_4)
                        ),
                    horizontalArrangement = Arrangement.End
                ) {

                    Text(
                        text = str(R.string.unknown),
                        fontFamily = Beirut_Medium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = dimen(R.dimen.space_1))
                    )

                    Text(
                        text = if (isSaleInvoice) str(R.string.buyer) else str(R.string.seller),
                        fontWeight = FontWeight.Medium,
                        fontFamily = Beirut_Medium,
                        modifier = Modifier.padding(horizontal = dimen(R.dimen.space_1))
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.face_24px),
                        contentDescription = "User",
                        modifier = Modifier
                            .padding(start = dimen(R.dimen.space_2))
                            .size(dimen(R.dimen.size_sm))
                    )
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
            onClose = { }
        )
    }
}