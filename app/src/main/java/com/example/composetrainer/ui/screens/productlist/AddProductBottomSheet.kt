package com.example.composetrainer.ui.screens.productlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.utils.BarcodeGenerator


@Composable
fun AddProductBottomSheet(
    initialProduct: Product? = null, // Null = Add mode, Non-null = Edit mode
    onSave: (Product) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var categoryID by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = if (initialProduct == null) "Add Product" else "Edit Product",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Product Name
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Product Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Barcode
        OutlinedTextField(
            value = barcode,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() } && newValue.length <= 12) {
                    barcode = newValue
                }
            },
            label = { Text("Barcode (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Price
        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Category ID
        OutlinedTextField(
            value = categoryID,
            onValueChange = { categoryID = it },
            label = { Text("Category ID (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val newProduct = Product(
                    id = initialProduct?.id ?: 0, // Use existing ID if editing
                    name = name,
                    barcode = BarcodeGenerator.generateBarcodeNumber(),
                    price = price.toLongOrNull(),
                    image = null,
                    categoryID = categoryID.toIntOrNull(),
                    date = initialProduct?.date ?: System.currentTimeMillis(),
                    stock = initialProduct?.stock ?: 0
                )
                onSave(newProduct)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (initialProduct == null) "Add Product" else "Save Changes",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewAddProductBottomSheet() {
//    ComposeTrainerTheme {
//        AddProductBottomSheet(
//            onAddProduct = {},
//            onDismiss = {}
//        )
//    }
//}