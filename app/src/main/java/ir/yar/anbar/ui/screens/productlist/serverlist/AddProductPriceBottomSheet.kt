package ir.yar.anbar.ui.screens.productlist.serverlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ir.yar.anbar.R
import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.model.ProductFactory
import ir.yar.anbar.ui.screens.productlist.AddProductTopBar
import ir.yar.anbar.ui.screens.productlist.PriceField
import ir.yar.anbar.ui.screens.productlist.SaveButton
import ir.yar.anbar.ui.theme.BHoma
import ir.yar.anbar.ui.theme.BKoodak
import ir.yar.anbar.ui.theme.color.costPrice
import ir.yar.anbar.ui.theme.color.salePrice
import ir.yar.anbar.utils.barcode.BarcodeGenerator
import ir.yar.anbar.utils.dimen
import ir.yar.anbar.utils.dimenTextSize
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductPriceBottomSheet(
    product: Product,
    onDismiss: () -> Unit,
    onSave: (salePrice: Long, costPrice: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var salePriceText by remember { mutableStateOf("") }
    var costPriceText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(
            topStart = dimen(R.dimen.radius_lg),
            topEnd = dimen(R.dimen.radius_lg)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Header
            AddProductTopBar(
                onNavigateBack = onDismiss
            )

            Spacer(modifier = Modifier.height(16.dp))


            // ── Header Row: Menu + Image + Name ──────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // ── Product Image ─────────────────────────────────────
                ProductThumbnail(imageUrl = product.image?.value)

                Spacer(modifier = Modifier.width(12.dp))

                // Product Name
                Text(
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start,
                    text = product.name.value,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = BKoodak,
                    fontWeight = FontWeight.Bold
                )


            }

            Spacer(modifier = Modifier.height(16.dp))


            // ── Category + Barcode Row ────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {

                    Icon(
                        painter = painterResource(id = R.drawable.barcode_24px),
                        contentDescription = "barcode"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    product.barcode?.value?.let {
                        Text(
                            text = it,
                            fontSize = dimenTextSize(R.dimen.text_size_md),
                            fontFamily = BHoma
                        )
                    } ?: Text(
                        text = "N/A",
                        fontSize = dimenTextSize(R.dimen.text_size_md),
                        fontFamily = BHoma
                    )

                }


                Row(verticalAlignment = Alignment.CenterVertically) {

                    Icon(
                        imageVector = Icons.Default.Category,
                        contentDescription = "Category",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = product.subcategoryId?.value.toString(),
                        fontSize = dimenTextSize(R.dimen.text_size_md),
                        fontFamily = BHoma
                    )
                }

            }

            Spacer(modifier = Modifier.height(16.dp))

            // Cost Price Field
            PriceField(
                value = costPriceText,
                onValueChange = { costPriceText = it.replace(",", "") },
                label = R.string.cost_price,
                iconRes = R.drawable.input_circle_24px,
                colorScheme = MaterialTheme.colorScheme.costPrice,
                focusRequester = focusRequester
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sale Price Field
            PriceField(
                value = salePriceText,
                onValueChange = { salePriceText = it.replace(",", "") },
                label = R.string.sale_price,
                iconRes = R.drawable.output_circle_24px,
                colorScheme = MaterialTheme.colorScheme.salePrice
            )

            Spacer(modifier = Modifier.height(16.dp))


            // Save Button
            SaveButton(
                onSave = {
                    val salePrice = salePriceText.toLongOrNull() ?: 0L
                    val costPrice = costPriceText.toLongOrNull() ?: 0L
                    onSave(salePrice, costPrice)
                },
                enabled = salePriceText.isNotBlank() && costPriceText.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )

        }
    }
}
