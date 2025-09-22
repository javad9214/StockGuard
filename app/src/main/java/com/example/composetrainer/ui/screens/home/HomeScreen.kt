package com.example.composetrainer.ui.screens.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.composetrainer.R
import com.example.composetrainer.ui.navigation.Screen
import com.example.composetrainer.ui.theme.Beirut_Medium
import com.example.composetrainer.ui.viewmodels.home.HomeViewModel
import com.example.composetrainer.ui.viewmodels.InvoiceViewModel
import com.example.composetrainer.ui.viewmodels.home.HomeTotalItemsViewModel
import com.example.composetrainer.utils.dateandtime.FarsiDateUtil
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.dimenTextSize
import com.example.composetrainer.utils.str


@Composable
fun HomeScreen(
    onAlertClick: () -> Unit,
    onButtonClick: () -> Unit,
    onToggleTheme: () -> Unit = {},
    onTodayButtonClick: () -> Unit = {},
    isDarkTheme: Boolean = false,
    navController: NavController = rememberNavController(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    invoiceViewModel: InvoiceViewModel = hiltViewModel(),
    homeTotalItemsViewModel: HomeTotalItemsViewModel = hiltViewModel()

) {

    // most Sold Product Details
    val mostSoldProducts by homeTotalItemsViewModel.products.collectAsState()
    val mostSoldProductsSummery by homeTotalItemsViewModel.productSalesSummaryList.collectAsState()

    // total Items Details
    val totalInvoiceCount by homeTotalItemsViewModel.totalInvoiceCount.collectAsState()
    val totalSales by homeTotalItemsViewModel.totalSoldPrice.collectAsState()
    val totalProfit by homeTotalItemsViewModel.totalProfitPrice.collectAsState()

    val persianDate = remember { FarsiDateUtil.getTodayFormatted() }
    var showBottomSheet by remember { mutableStateOf(false) }
    var showBarcodeScannerView by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Observe scanned product
    val scannedProduct by homeViewModel.scannedProduct.collectAsState()


    // Handle navigation when product is found
    LaunchedEffect(scannedProduct) {
        scannedProduct?.let { product ->
            // Add debug log to verify product is found and being added
            Log.d(TAG, "Product found: ${product.name}, ID: ${product.id}, adding to invoice")

            // Add product to current invoice
            invoiceViewModel.addToCurrentInvoice(product, 1)

            // Check if product was added to invoice
            val currentInvoiceItems = invoiceViewModel.currentInvoice.value

            // Navigate to invoice screen
            navController.navigate(Screen.Invoice.route)

            // Clear the scanned product
            homeViewModel.clearScannedProduct()
        }
    }


    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(start = dimen(R.dimen.space_6), end = dimen(R.dimen.space_2)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = persianDate,
                fontFamily = Beirut_Medium,
                fontSize = dimenTextSize(R.dimen.text_size_xl)
            )

            Row {

                IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.settings_24px),
                        contentDescription = "Navigate to Settings"
                    )
                }

                IconButton(onClick = onAlertClick) {

                    Icon(
                        painter = painterResource(id = R.drawable.notifications_24px),
                        contentDescription = "Notifications"
                    )
                }

                IconButton(onClick = onAlertClick) {

                    Icon(
                        painter = painterResource(id = R.drawable.account_circle_24px),
                        contentDescription = "Notifications"
                    )
                }
            }

        }

        Spacer(modifier = Modifier.height(dimen(R.dimen.space_5)))

        Card(
            modifier = Modifier
                .height(dimen(R.dimen.size_lg))
                .wrapContentWidth()
                .align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(dimen(R.dimen.radius_xxl)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            onClick = onTodayButtonClick
        ) {

            Row(
                modifier = Modifier
                    .padding(dimen(R.dimen.space_1)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(end = dimen(R.dimen.space_1), start = dimen(R.dimen.space_4)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = str(R.string.today),
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = Beirut_Medium,
                        fontSize = dimenTextSize(R.dimen.text_size_lg)
                    )
                }

                Spacer(modifier = Modifier.width(dimen(R.dimen.space_1)))

                Icon(
                    modifier = Modifier.padding(end = dimen(R.dimen.space_1)),
                    painter = painterResource(id = R.drawable.keyboard_arrow_down_24px),
                    contentDescription = "down",
                )

            }
        }

        Spacer(modifier = Modifier.height(dimen(R.dimen.space_4)))

        Log.i(TAG, "HomeScreen: total sale ${totalSales.amount} total profit ${totalProfit.amount}")

        TotalsItem(
            modifier = Modifier,
            totalInvoiceCount = totalInvoiceCount,
            totalSales = totalSales,
            totalProfit = totalProfit
        )

        Spacer(modifier = Modifier.height(dimen(R.dimen.space_4)))

        Text(
            modifier = Modifier.padding(start = dimen(R.dimen.space_4)),
            text = str(R.string.most_sold_products),
            style = MaterialTheme.typography.bodyLarge,
            fontFamily = Beirut_Medium,
            fontSize = dimenTextSize(R.dimen.text_size_lg)
        )

        Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = dimen(R.dimen.space_1))
        ) {
            items(mostSoldProductsSummery, key = { it.id.value }) { mostSoldProductsSummery ->
                MostSoldProductItem(
                    product = mostSoldProducts.find { it.id == mostSoldProductsSummery.productId} ?: return@items,
                    productSalesSummary = mostSoldProductsSummery
                )
            }
        }
    }

}

const val TAG = "HomeScreen"

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    // We can't provide real viewmodel in preview, so we'll skip it
    // This preview is primarily to check the UI layout
}
