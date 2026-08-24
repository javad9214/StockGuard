package ir.yar.anbar.ui.screens.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ir.yar.anbar.R
import ir.yar.anbar.ui.navigation.Screen
import ir.yar.anbar.ui.screens.component.DatePickerBottomDialog
import ir.yar.anbar.ui.theme.Beirut_Medium
import ir.yar.anbar.ui.theme.ComposeTrainerTheme
import ir.yar.anbar.ui.theme.color.customError
import ir.yar.anbar.ui.viewmodels.InvoiceViewModel
import ir.yar.anbar.ui.viewmodels.ProfileViewModel
import ir.yar.anbar.ui.viewmodels.home.HomeTotalItemsViewModel
import ir.yar.anbar.ui.viewmodels.home.HomeViewModel
import ir.yar.anbar.utils.dateandtime.FarsiDateUtil
import ir.yar.anbar.utils.dateandtime.TimeRange
import ir.yar.anbar.utils.dimen
import ir.yar.anbar.utils.dimenTextSize
import ir.yar.anbar.utils.str

private const val TAG = "HomeScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAlertClick: () -> Unit,
    onButtonClick: () -> Unit,
    onProfileClick: () -> Unit = {},
    onToggleTheme: () -> Unit = {},
    onTodayButtonClick: () -> Unit = {},
    onLogout: () -> Unit = {},
    isDarkTheme: Boolean = false,
    navController: NavController = rememberNavController(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    invoiceViewModel: InvoiceViewModel = hiltViewModel(),
    // Deliberately destination-scoped (NavBackStackEntry) — unlike homeViewModel,
    // which MainScreen shares across screens for barcode scanning. This VM's
    // selected period must not leak into other screens.
    homeTotalItemsViewModel: HomeTotalItemsViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {

    // Single state collection - much cleaner!
    val uiState by homeTotalItemsViewModel.uiState.collectAsState()

    val persianDate = remember { FarsiDateUtil.getTodayFormatted() }

    var showDatePickerBottomSheet by remember { mutableStateOf(false) }
    val datePickerSheetState = rememberModalBottomSheetState()
    var selectedDate by remember { mutableStateOf(TimeRange.TODAY) }

    var showProfileMenu by remember { mutableStateOf(false) }
    val profileState by profileViewModel.uiState.collectAsState()

    // Fetch the profile the first time the dropdown opens
    LaunchedEffect(showProfileMenu) {
        if (showProfileMenu) profileViewModel.loadUserProfile()
    }

    // Observe barcode-scan state (product, loading, error, barcode as one snapshot)
    val scanState by homeViewModel.uiState.collectAsState()

    // Handle navigation when product is found
    LaunchedEffect(scanState.scannedProduct) {
        scanState.scannedProduct?.let { product ->
            Log.d(TAG, "Product found: ${product.name}, ID: ${product.id}, adding to invoice")
            invoiceViewModel.addToCurrentInvoice(product, 1)
            navController.navigate(Screen.Invoice.route)
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
                        contentDescription = str(R.string.navigate_to_settings)
                    )
                }

                IconButton(onClick = onAlertClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.notifications_24px),
                        contentDescription = str(R.string.notifications)
                    )
                }

                Box {
                    IconButton(onClick = {
                        onProfileClick()
                        showProfileMenu = true
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.account_circle_24px),
                            contentDescription = str(R.string.profile)
                        )
                    }

                    ProfileDropdown(
                        expanded = showProfileMenu,
                        onDismissRequest = { showProfileMenu = false },
                        state = profileState,
                        onRetry = { profileViewModel.loadUserProfile(forceRefresh = true) },
                        onLogout = onLogout
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(dimen(R.dimen.space_5)))

        // Section emptiness for the selected period, independent of loading —
        // it drives the skeleton vs empty-state decision and the refresh indicator
        val isEmpty = uiState.analytics.totalInvoiceCount == 0 &&
                uiState.products.topSellingProducts.isEmpty() &&
                uiState.products.topProfitableProducts.isEmpty() &&
                uiState.products.lowStockProducts.isEmpty()

        // Pull-to-refresh reloads the currently selected period; the spinner is
        // suppressed on first loads, where the skeleton already shows progress
        PullToRefreshBox(
            isRefreshing = uiState.isLoading && !isEmpty,
            onRefresh = { homeTotalItemsViewModel.reLoadProductSaleSummary(selectedDate) },
            modifier = Modifier.fillMaxSize()
        ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ElevatedCard(
                modifier = Modifier
                    .height(dimen(R.dimen.size_lg))
                    .wrapContentWidth()
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(dimen(R.dimen.radius_xxl)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                onClick = { showDatePickerBottomSheet = true }
            ) {
                Row(
                    modifier = Modifier.padding(dimen(R.dimen.space_1)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(end = dimen(R.dimen.space_1), start = dimen(R.dimen.space_4)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = str(selectedDate.getResourceId()),
                            style = MaterialTheme.typography.bodyLarge,
                            fontFamily = Beirut_Medium,
                            fontSize = dimenTextSize(R.dimen.text_size_lg)
                        )
                    }

                    Spacer(modifier = Modifier.width(dimen(R.dimen.space_1)))

                    Icon(
                        modifier = Modifier.padding(end = dimen(R.dimen.space_1)),
                        painter = painterResource(id = R.drawable.keyboard_arrow_down_24px),
                        // Same icon/semantic as the AnalyzeScreen range selector
                        contentDescription = str(R.string.select_time_range),
                    )
                }
            }

            if (showDatePickerBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showDatePickerBottomSheet = false },
                    sheetState = datePickerSheetState,
                    dragHandle = { BottomSheetDefaults.DragHandle() },
                    containerColor = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(
                        topStart = dimen(R.dimen.radius_lg),
                        topEnd = dimen(R.dimen.radius_lg)
                    )
                ) {
                    DatePickerBottomDialog(
                        selectedItem = selectedDate,
                        onNewSelected = { timeRange ->
                            selectedDate = timeRange
                            showDatePickerBottomSheet = false
                            homeTotalItemsViewModel.reLoadProductSaleSummary(timeRange)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimen(R.dimen.space_4)))

            // A failed load must not be mistaken for "no data", and the first load
            // must not render as empty sections — so error and loading win over isEmpty
            val errorMessage = uiState.errorMessage
            if (errorMessage != null) {
                HomeErrorState(
                    message = errorMessage,
                    onRetry = { homeTotalItemsViewModel.reLoadProductSaleSummary(selectedDate) },
                    modifier = Modifier.fillMaxWidth()
                )
            } else if (uiState.isLoading && isEmpty) {
                HomeLoadingState(modifier = Modifier.fillMaxWidth())
            } else if (isEmpty) {
                HomeEmptyState(modifier = Modifier.fillMaxWidth())
            } else {
                // Analytics section - using combined state
                TotalsItem(
                    modifier = Modifier,
                    totalInvoiceCount = uiState.analytics.totalInvoiceCount,
                    totalSales = uiState.analytics.totalSales,
                    totalProfit = uiState.analytics.totalProfit
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

                // Most Sold Products - NO MORE .find()!
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = dimen(R.dimen.space_1))
                ) {
                    items(
                        items = uiState.products.topSellingProducts,
                        key = { it.summary.id.value }
                    ) { productWithSummary ->
                        MostSoldProductItem(
                            product = productWithSummary.product,
                            productSalesSummary = productWithSummary.summary,
                            rank = productWithSummary.rank
                        )
                    }
                }

                Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))

                Text(
                    modifier = Modifier.padding(start = dimen(R.dimen.space_4)),
                    text = str(R.string.most_profitable_products),
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = Beirut_Medium,
                    fontSize = dimenTextSize(R.dimen.text_size_lg)
                )

                Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))

                // Most Profitable Products - NO MORE .find() or .indexOf()!
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = dimen(R.dimen.space_1))
                ) {
                    items(
                        items = uiState.products.topProfitableProducts,
                        key = { it.summary.id.value }
                    ) { productWithSummary ->
                        MostSoldProductItem(
                            product = productWithSummary.product,
                            productSalesSummary = productWithSummary.summary,
                            rank = productWithSummary.rank
                        )
                    }
                }

                Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))

                Text(
                    modifier = Modifier.padding(start = dimen(R.dimen.space_4)),
                    text = str(R.string.stock_running_out),
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = Beirut_Medium,
                    fontSize = dimenTextSize(R.dimen.text_size_lg)
                )

                Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))

                // Low Stock Products
                LazyRow {
                    items(
                        items = uiState.products.lowStockProducts,
                        key = { it.id.value }
                    ) { product ->
                        LowStockProductItem(product = product)
                    }
                }
            }

            Spacer(modifier = Modifier.height(dimen(R.dimen.space_8)))
        }
        } // PullToRefreshBox
    }
}

@Composable
fun HomeEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dimen(R.dimen.space_8)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(dimen(R.dimen.size_6xl))
                .clip(RoundedCornerShape(dimen(R.dimen.radius_circle)))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.box),
                contentDescription = str(R.string.home_empty_icon),
                modifier = Modifier.size(dimen(R.dimen.size_4xl)),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(modifier = Modifier.height(dimen(R.dimen.space_5)))

        Text(
            text = str(R.string.home_empty_title),
            style = MaterialTheme.typography.titleLarge,
            fontFamily = Beirut_Medium,
            fontSize = dimenTextSize(R.dimen.text_size_xl),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))

        Text(
            text = str(R.string.home_empty_message),
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = Beirut_Medium,
            fontSize = dimenTextSize(R.dimen.text_size_md),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = dimen(R.dimen.space_8))
        )
    }
}

@Composable
fun HomeErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dimen(R.dimen.space_8)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(dimen(R.dimen.size_6xl))
                .clip(RoundedCornerShape(dimen(R.dimen.radius_circle)))
                .background(MaterialTheme.colorScheme.customError.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.error_24px),
                contentDescription = null,
                modifier = Modifier.size(dimen(R.dimen.size_4xl)),
                tint = MaterialTheme.colorScheme.customError
            )
        }

        Spacer(modifier = Modifier.height(dimen(R.dimen.space_5)))

        Text(
            text = str(R.string.home_error_title),
            style = MaterialTheme.typography.titleLarge,
            fontFamily = Beirut_Medium,
            fontSize = dimenTextSize(R.dimen.text_size_xl),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = Beirut_Medium,
            fontSize = dimenTextSize(R.dimen.text_size_md),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = dimen(R.dimen.space_8))
        )

        Spacer(modifier = Modifier.height(dimen(R.dimen.space_4)))

        TextButton(onClick = onRetry) {
            Icon(
                painter = painterResource(id = R.drawable.refresh_24px),
                contentDescription = null,
                modifier = Modifier.size(dimen(R.dimen.size_xs))
            )
            Spacer(modifier = Modifier.width(dimen(R.dimen.space_1)))
            Text(
                text = str(R.string.retry),
                fontFamily = Beirut_Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeEmptyStatePreview() {
    ComposeTrainerTheme {
        HomeEmptyState(modifier = Modifier.fillMaxWidth())
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeErrorStatePreview() {
    ComposeTrainerTheme {
        HomeErrorState(
            message = "No product found with barcode: 123456789",
            onRetry = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}