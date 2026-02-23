package ir.yar.anbar.ui.screens.productlist

import android.util.Log
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ir.yar.anbar.R
import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.model.ProductFactory
import ir.yar.anbar.ui.screens.component.CurrencyIcon
import ir.yar.anbar.ui.theme.BKoodak
import ir.yar.anbar.ui.theme.Beirut_Medium
import ir.yar.anbar.ui.theme.color.costPrice
import ir.yar.anbar.ui.theme.color.salePrice
import ir.yar.anbar.ui.viewmodels.ProductsViewModel
import ir.yar.anbar.utils.barcode.BarcodeGenerator
import ir.yar.anbar.utils.dimen
import ir.yar.anbar.utils.dimenTextSize
import ir.yar.anbar.utils.price.ThousandSeparatorTransformation

@Composable
fun AddProduct(
    initialBarcode: String? = null,
    productId: Long? = null,
    onSave: (Product) -> Unit,
    onNavigateBack: () -> Unit,
    productsViewModel: ProductsViewModel = hiltViewModel(),
) {


    // Fetch product if editing
    LaunchedEffect(productId) {
        productId?.let { productsViewModel.getProductById(it) }
    }

    val product by productsViewModel.selectedProduct.collectAsState()

    Log.i("AddProduct", "AddProduct: initialBarcode: $initialBarcode, productId: $productId")

    // Initialize form fields with product data or defaults
    var name by remember(product) {
        mutableStateOf(product?.name?.value ?: "")
    }

    var barcode by remember(product, initialBarcode) {
        mutableStateOf(initialBarcode ?: product?.barcode?.value ?: "")
    }

    var salePrice by remember(product) {
        mutableStateOf(product?.price?.amount?.toString() ?: "")
    }

    var costPrice by remember(product) {
        mutableStateOf(product?.costPrice?.amount?.toString() ?: "")
    }

    var subcategoryId by remember(product) {
        mutableStateOf(product?.subcategoryId?.value?.toString() ?: "")
    }

    val isEditMode = product != null

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Header with title and close button
            TopBar(
                isEditMode = isEditMode,
                onNavigateBack = onNavigateBack
            )

            Spacer(modifier = Modifier.height(dimen(R.dimen.space_1)))

            Column(
                modifier = Modifier.padding(horizontal = dimen(R.dimen.space_4))
            ) {
                // Product Name Field
                ProductNameField(
                    value = name,
                    onValueChange = { name = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Barcode Field
                BarcodeField(
                    value = barcode,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() } && newValue.length <= 12) {
                            barcode = newValue
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Cost Price Field
                PriceField(
                    value = costPrice,
                    onValueChange = { costPrice = it.replace(",", "") },
                    label = R.string.cost_price,
                    iconRes = R.drawable.input_circle_24px,
                    colorScheme = MaterialTheme.colorScheme.costPrice
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Sale Price Field
                PriceField(
                    value = salePrice,
                    onValueChange = { salePrice = it.replace(",", "") },
                    label = R.string.sale_price,
                    iconRes = R.drawable.output_circle_24px,
                    colorScheme = MaterialTheme.colorScheme.salePrice
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Subcategory ID Field
                SubcategoryField(
                    value = subcategoryId,
                    onValueChange = { subcategoryId = it }
                )
            }
        }

        // Save Button
        SaveButton(
            isEditMode = isEditMode,
            onSave = {
                val newProduct = ProductFactory.createComplete(
                    id = product?.id?.value ?: 0,
                    name = name,
                    barcode = barcode.ifEmpty { BarcodeGenerator.generateBarcodeNumber() },
                    price = salePrice.toLongOrNull() ?: 0,
                    costPrice = costPrice.toLongOrNull() ?: 0,
                    description = product?.description?.value ?: "",
                    subcategoryId = subcategoryId.toIntOrNull() ?: product?.subcategoryId?.value ?: 0,
                    supplierId = product?.supplierId?.value ?: 0,
                    unit = product?.unit?.value ?: "",
                    initialStock = product?.stock?.value ?: 0,
                    minStockLevel = product?.minStockLevel?.value ?: 0,
                    maxStockLevel = product?.maxStockLevel?.value ?: 0,
                    tags = product?.tags?.value ?: ""
                )
                onSave(newProduct)
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun TopBar(
    isEditMode: Boolean,
    onNavigateBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(start = dimen(R.dimen.space_6), end = dimen(R.dimen.space_2)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(
                id = if (isEditMode) R.string.edit_product_title else R.string.add_product_title
            ),
            fontFamily = Beirut_Medium,
            fontSize = dimenTextSize(R.dimen.text_size_xl)
        )

        // Close icon
        Box(
            modifier = Modifier
                .padding(dimen(R.dimen.space_2))
                .size(dimen(R.dimen.size_lg))
                .clip(CircleShape)
                .background(Color.Gray.copy(alpha = 0.08f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = LocalIndication.current,
                    onClick = onNavigateBack
                )
                .padding(dimen(R.dimen.space_2))
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun ProductNameField(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                stringResource(R.string.product_name),
                fontFamily = BKoodak,
                fontWeight = FontWeight.Bold
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        singleLine = true
    )
}

@Composable
private fun BarcodeField(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    modifier = Modifier.size(dimen(R.dimen.size_xs)),
                    painter = painterResource(id = R.drawable.barcode_24px),
                    contentDescription = "Barcode Icon"
                )
                Spacer(modifier = Modifier.width(dimen(R.dimen.space_2)))
                Text(
                    stringResource(R.string.barcode_optional),
                    fontFamily = BKoodak,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        singleLine = true
    )
}

@Composable
private fun PriceField(
    value: String,
    onValueChange: (String) -> Unit,
    label: Int,
    iconRes: Int,
    colorScheme: Color
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        trailingIcon = {
            CurrencyIcon(
                contentDescription = "Currency Icon",
                tint = colorScheme,
                modifier = Modifier.size(dimen(R.dimen.size_sm))
            )
        },
        label = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    modifier = Modifier
                        .size(dimen(R.dimen.size_xs))
                        .rotate(90f),
                    painter = painterResource(id = iconRes),
                    contentDescription = stringResource(label),
                    tint = colorScheme
                )
                Spacer(modifier = Modifier.width(dimen(R.dimen.space_2)))
                Text(
                    stringResource(label),
                    fontFamily = BKoodak,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        visualTransformation = ThousandSeparatorTransformation(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        singleLine = true,
        textStyle = TextStyle(
            fontFamily = BKoodak,
            fontWeight = FontWeight.Bold,
            fontSize = dimenTextSize(R.dimen.text_size_md)
        )
    )
}

@Composable
private fun SubcategoryField(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                stringResource(R.string.category_id_optional),
                fontFamily = BKoodak,
                fontWeight = FontWeight.Bold
            )
        },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        singleLine = true
    )
}

@Composable
private fun SaveButton(
    isEditMode: Boolean,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onSave,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimen(R.dimen.space_6), vertical = dimen(R.dimen.space_4)),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(
            modifier = Modifier.padding(vertical = dimen(R.dimen.space_1)),
            text = stringResource(
                id = if (isEditMode) R.string.save_changes_button else R.string.add_product_button
            ),
            fontFamily = Beirut_Medium,
            fontWeight = FontWeight.Bold,
            fontSize = dimenTextSize(R.dimen.text_size_lg)
        )
    }
}

// ==================== Previews ====================

@Preview(showBackground = true, name = "Add Product")
@Composable
fun AddProductPreview() {
    MaterialTheme {
        AddProduct(
            initialBarcode = null,
            productId = null,
            onSave = { /* Preview - no action */ },
            onNavigateBack = { /* Preview - no action */ }
        )
    }
}

@Preview(showBackground = true, name = "Add Product with Barcode")
@Composable
fun AddProductWithBarcodePreview() {
    MaterialTheme {
        AddProduct(
            initialBarcode = "987654321098",
            productId = null,
            onSave = { /* Preview - no action */ },
            onNavigateBack = { /* Preview - no action */ }
        )
    }
}