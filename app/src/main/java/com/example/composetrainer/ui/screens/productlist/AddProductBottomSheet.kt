package com.example.composetrainer.ui.screens.productlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.composetrainer.R
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.model.ProductFactory
import com.example.composetrainer.ui.screens.component.CurrencyIcon
import com.example.composetrainer.ui.theme.BKoodak
import com.example.composetrainer.ui.theme.Beirut_Medium
import com.example.composetrainer.ui.theme.costPrice
import com.example.composetrainer.ui.theme.salePrice
import com.example.composetrainer.utils.barcode.BarcodeGenerator
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.dimenTextSize
import com.example.composetrainer.utils.price.ThousandSeparatorTransformation

@Composable
fun AddProductBottomSheet(
    initialProduct: Product? = null, // Null = Add mode, Non-null = Edit mode
    initialBarcode: String? = null,
    onSave: (Product) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialProduct?.name?.value ?: "") }
    var barcode by remember {
        mutableStateOf(
            initialBarcode ?: initialProduct?.barcode?.value ?: ""
        )
    }
    var salePrice by remember { mutableStateOf(initialProduct?.price?.amount?.toString() ?: "") }
    var costPrice by remember {
        mutableStateOf(
            initialProduct?.costPrice?.amount?.toString() ?: ""
        )
    }
    var subcategoryId by remember {
        mutableStateOf(
            initialProduct?.subcategoryId?.value?.toString() ?: ""
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(
                    id = if (initialProduct == null) R.string.add_product_title else R.string.edit_product_title
                ),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                fontFamily = Beirut_Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Divider(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Product Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
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

            Spacer(modifier = Modifier.height(16.dp))

            // Barcode
            OutlinedTextField(
                value = barcode,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() } && newValue.length <= 12) {
                        barcode = newValue
                    }
                },
                label = {
                    Row( verticalAlignment = Alignment.CenterVertically){

                        Icon(
                            modifier = Modifier
                                .size(dimen(R.dimen.size_xs)),
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

            Spacer(modifier = Modifier.height(16.dp))

            // Cost Price
            OutlinedTextField(
                value = costPrice,
                onValueChange = { newText ->
                    // store raw number (without commas) in state
                    costPrice = newText.replace(",", "")
                },
                trailingIcon = {
                    CurrencyIcon(
                        contentDescription = "Currency Icon",
                        tint = MaterialTheme.colorScheme.costPrice,
                        modifier = Modifier
                            .size(dimen(R.dimen.size_sm))
                    )
                },
                label = {
                    Row( verticalAlignment = Alignment.CenterVertically) {

                        Icon(
                            modifier = Modifier
                                .size(dimen(R.dimen.size_xs))
                                .rotate(90f),
                            painter = painterResource(id = R.drawable.input_circle_24px),
                            contentDescription = "Cost Icon",
                            tint = MaterialTheme.colorScheme.costPrice
                        )

                        Spacer(modifier = Modifier.width(dimen(R.dimen.space_2)))

                        Text(
                            stringResource(R.string.cost_price),
                            fontFamily = BKoodak,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.costPrice
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

            Spacer(modifier = Modifier.height(16.dp))

            // Sale Price
            OutlinedTextField(
                value = salePrice,
                onValueChange = { newText ->
                    // store raw number (without commas) in state
                    salePrice = newText.replace(",", "")
                },
                trailingIcon = {
                    CurrencyIcon(
                        contentDescription = "Currency Icon",
                        tint = MaterialTheme.colorScheme.salePrice,
                        modifier = Modifier
                            .size(dimen(R.dimen.size_sm))
                    )
                },
                label = {
                    Row ( verticalAlignment = Alignment.CenterVertically) {

                        Icon(
                            modifier = Modifier
                                .size(dimen(R.dimen.size_xs))
                                .rotate(90f),
                            tint = MaterialTheme.colorScheme.salePrice,
                            painter = painterResource(id = R.drawable.output_circle_24px),
                            contentDescription = "Sale Icon"
                        )

                        Spacer(modifier = Modifier.width(dimen(R.dimen.space_2)))

                        Text(
                            stringResource(R.string.sale_price),
                            fontFamily = BKoodak,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.salePrice
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

            Spacer(modifier = Modifier.height(16.dp))

            // Subcategory ID
            OutlinedTextField(
                value = subcategoryId,
                onValueChange = { subcategoryId = it },
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

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val newProduct = ProductFactory.createComplete(
                        name = name,
                        barcode = barcode.ifEmpty { BarcodeGenerator.generateBarcodeNumber() },
                        price = salePrice.toLongOrNull() ?: 0,
                        costPrice = costPrice.toLongOrNull() ?: 0,
                        description = initialProduct?.description?.value ?: "",
                        subcategoryId = subcategoryId.toIntOrNull() ?: 0,
                        supplierId = initialProduct?.supplierId?.value ?: 0,
                        unit = initialProduct?.unit?.value ?: "",
                        initialStock = initialProduct?.stock?.value ?: 0,
                        minStockLevel = initialProduct?.minStockLevel?.value ?: 0,
                        maxStockLevel = initialProduct?.maxStockLevel?.value ?: 0,
                        tags = initialProduct?.tags?.value ?: ""
                    )

                    onSave(newProduct)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = name.isNotBlank()
            ) {
                Text(
                    text = stringResource(
                        id = if (initialProduct == null) R.string.add_product_button else R.string.save_changes_button
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Beirut_Medium
                )
            }
        }
    }
}
