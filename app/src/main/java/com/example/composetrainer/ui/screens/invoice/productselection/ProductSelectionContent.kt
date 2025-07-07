package com.example.composetrainer.ui.screens.invoice.productselection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.composetrainer.R
import com.example.composetrainer.domain.model.Product

@Composable
fun ProductSelectionContent(
    products: List<Product>,
    onProductSelected: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = stringResource(R.string.select_product),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
        }
        
        ProductSelectionList(
            products = products,
            onProductSelected = onProductSelected
        )
    }
}

@Composable
private fun ProductSelectionList(
    products: List<Product>,
    onProductSelected: (Product) -> Unit
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    
    LazyColumn(
        modifier = Modifier
            .heightIn(max = screenHeight * 0.5f)
            .clip(RoundedCornerShape(12.dp))
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products, key = { it.id }) { product ->
            ProductSelectionItem(
                product = product,
                onClick = { onProductSelected(product) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductSelectionContentPreview() {
    val sampleProducts = listOf(
        Product(
            id = 1L,
            name = "گوشی هوشمند",
            barcode = "123456789",
            price = 69999L,
            image = null,
            subCategoryId = 1,
            date = System.currentTimeMillis(),
            stock = 15,
            costPrice = null,
            description = null,
            supplierId = null,
            unit = null,
            minStockLevel = null,
            maxStockLevel = null,
            isActive = true,
            tags = null,
            lastSoldDate = null
        ),
        Product(
            id = 2L,
            name = "هدفون بی‌سیم",
            barcode = "987654321",
            price = 14999L,
            image = null,
            subCategoryId = 2,
            date = System.currentTimeMillis(),
            stock = 8,
            costPrice = null,
            description = null,
            supplierId = null,
            unit = null,
            minStockLevel = null,
            maxStockLevel = null,
            isActive = true,
            tags = null,
            lastSoldDate = null
        ),
        Product(
            id = 3L,
            name = "لپ‌تاپ",
            barcode = "456789123",
            price = 99999L,
            image = null,
            subCategoryId = 1,
            date = System.currentTimeMillis(),
            stock = 5,
            costPrice = null,
            description = null,
            supplierId = null,
            unit = null,
            minStockLevel = null,
            maxStockLevel = null,
            isActive = true,
            tags = null,
            lastSoldDate = null
        )
    )

    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        androidx.compose.runtime.CompositionLocalProvider(
            LocalLayoutDirection provides LayoutDirection.Rtl
        ) {
            ProductSelectionContent(
                products = sampleProducts,
                onProductSelected = {},
                modifier = Modifier
                    .padding(16.dp)
            )
        }
    }
}