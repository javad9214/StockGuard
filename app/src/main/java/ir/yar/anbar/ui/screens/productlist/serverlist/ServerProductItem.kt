package ir.yar.anbar.ui.screens.productlist.serverlist

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import ir.yar.anbar.R
import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.ui.screens.component.CurrencyIcon
import ir.yar.anbar.ui.theme.BHoma
import ir.yar.anbar.ui.theme.BMitra
import ir.yar.anbar.ui.theme.Beirut_Medium
import ir.yar.anbar.utils.dimen
import ir.yar.anbar.utils.dimenTextSize
import ir.yar.anbar.utils.price.PriceValidator
import ir.yar.anbar.utils.str

@Composable
fun ServerProductItem(
    product: Product,
    onAdd: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onProductClick: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }
    var itemClicked by remember { mutableStateOf(false) }

    val myFontFamily = FontFamily(
        Font(R.font.b_koodak_bd, FontWeight.Normal)
    )

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .clickable { onProductClick() },
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // ── Header Row: Menu + Image + Name ──────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Menu Icon
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit") },
                                onClick = { showMenu = false; onEdit() }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                onClick = { showMenu = false; onDelete() }
                            )
                        }
                    }

                    // Product Name
                    Text(
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End,
                        text = product.name.value,
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = myFontFamily
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // ── Product Image ─────────────────────────────────────
                    ProductThumbnail(imageUrl = product.image?.value)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ── Category + Barcode Row ────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = product.subcategoryId?.value.toString(),
                            fontSize = dimenTextSize(R.dimen.text_size_md),
                            fontFamily = BHoma
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = "Category",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
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
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.barcode_24px),
                            contentDescription = "barcode"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))

                // ── Bottom Row: Add Button + Price ────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { itemClicked = true; onAdd() },
                        shape = RoundedCornerShape(dimen(R.dimen.radius_sm)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (itemClicked) MaterialTheme.colorScheme.tertiary
                            else MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        )
                    ) {
                        if (itemClicked) {
                            Text(
                                text = str(R.string.product_added),
                                fontSize = dimenTextSize(R.dimen.text_size_md),
                                fontFamily = Beirut_Medium
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                painter = painterResource(R.drawable.check_24px),
                                contentDescription = null
                            )
                        } else {
                            Text(
                                text = str(R.string.add_to_my_products),
                                fontSize = dimenTextSize(R.dimen.text_size_md),
                                fontFamily = Beirut_Medium
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            textAlign = TextAlign.Right,
                            text = stringResource(id = R.string.suggestion_price),
                            fontSize = dimenTextSize(R.dimen.text_size_xs),
                            fontFamily = BMitra
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CurrencyIcon(
                                contentDescription = "Rial",
                                modifier = Modifier.size(dimen(R.dimen.size_sm))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                textAlign = TextAlign.Start,
                                text = PriceValidator.formatPrice(product.price.amount.toString()),
                                fontSize = dimenTextSize(R.dimen.text_size_md),
                                fontFamily = BHoma
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Reusable Thumbnail ────────────────────────────────────────────────────────
@Composable
private fun ProductThumbnail(
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(8.dp)
    val size = 64.dp

    if (imageUrl != null) {
        SubcomposeAsyncImage(
            model = imageUrl,
            contentDescription = "Product image",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size)
                .clip(shape),
            loading = {
                Box(
                    modifier = Modifier
                        .size(size)
                        .background(MaterialTheme.colorScheme.surfaceVariant, shape),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            error = {
                Box(
                    modifier = Modifier
                        .size(size)
                        .background(MaterialTheme.colorScheme.errorContainer, shape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.BrokenImage,
                        contentDescription = "Image error",
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        )
    } else {
        // Placeholder when no image URL
        Box(
            modifier = modifier
                .size(size)
                .background(MaterialTheme.colorScheme.surfaceVariant, shape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = "No image",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
