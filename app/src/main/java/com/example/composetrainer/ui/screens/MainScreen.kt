package com.example.composetrainer.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.composetrainer.ui.components.CustomNavigationBar
import com.example.composetrainer.ui.navigation.BottomNavItem
import com.example.composetrainer.ui.navigation.Routes
import com.example.composetrainer.ui.screens.home.HomeScreen
import com.example.composetrainer.ui.screens.invoice.invoicescreen.InvoiceScreen
import com.example.composetrainer.ui.screens.invoicelist.InvoiceDetailScreen
import com.example.composetrainer.ui.screens.invoicelist.InvoicesListScreen
import com.example.composetrainer.ui.screens.productlist.ProductDetailsScreen
import com.example.composetrainer.ui.screens.productlist.ProductScreen
import com.example.composetrainer.ui.screens.productlist.serverlist.ServerProductListScreen
import com.example.composetrainer.ui.theme.ComposeTrainerTheme
import com.example.composetrainer.ui.viewmodels.InvoiceListViewModel
import com.example.composetrainer.ui.viewmodels.home.HomeViewModel
import com.example.login.ui.screens.LoginScreen

@Composable
fun MainScreen(
    navController: NavHostController,
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {}
) {
    // Create a shared InvoiceViewModel instance
    val sharedInvoiceListViewModel: InvoiceListViewModel = hiltViewModel()

    // Create a shared HomeViewModel instance for barcode scanning
    val sharedHomeViewModel: HomeViewModel = hiltViewModel()

    val bottomNavItems = listOf(
        BottomNavItem("Invoices", Routes.INVOICES_LIST),
        BottomNavItem("Analyze", Routes.ANALYZE),
        BottomNavItem("Products", Routes.PRODUCTS_LIST),
        BottomNavItem("Home", Routes.HOME)
    )

    // Observe the current back stack entry
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Determine if bottom navigation should be visible for current route
    val shouldShowBottomNav by remember(currentRoute) {
        derivedStateOf {
            when (currentRoute) {
                Routes.HOME, Routes.PRODUCTS_LIST, Routes.INVOICES_LIST, Routes.ANALYZE -> true
                else -> false
            }
        }
    }


    Scaffold(       bottomBar = {
            if (shouldShowBottomNav) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    CustomNavigationBar(
                        navController = navController,
                        currentRoute = currentRoute,
                        onFabClick = {
                            navController.navigate(Routes.INVOICE_CREATE) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN,
            modifier = Modifier
                .padding(innerPadding)
        ) {
            composable(Routes.LOGIN) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.PRODUCTS_LIST) {
                ProductScreen(navController = navController)
            }

            composable(Routes.HOME) {
                HomeScreen(
                    onButtonClick = {
                        navController.navigate(Routes.INVOICES_LIST)
                    },
                    onAlertClick = {
                        //TODO implement Notifications
                    },
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme,
                    navController = navController,
                    homeViewModel = sharedHomeViewModel
                )
            }

            composable(Routes.INVOICES_LIST) {
                InvoicesListScreen(
                    onCreateNew = {
                        navController.navigate(Routes.INVOICE_CREATE)
                    },
                    onInvoiceClick = { invoiceId ->
                        navController.navigate(
                            Routes.INVOICE_DETAILS.replace(
                                "{invoiceId}",
                                invoiceId.toString()
                            )
                        )
                    }
                )
            }

            composable(Routes.INVOICE_CREATE) {
                InvoiceScreen(
                    onComplete = {
                        navController.popBackStack()
                    },
                    onClose = {
                        navController.popBackStack()
                    },
                    invoiceListViewModel = sharedInvoiceListViewModel,
                    homeViewModel = sharedHomeViewModel
                )
            }

            composable(
                route = Routes.INVOICE_DETAILS,
                arguments = listOf(navArgument("invoiceId") { type = NavType.LongType })
            ) { backStackEntry ->
                val invoiceId = backStackEntry.arguments?.getLong("invoiceId") ?: 0L
                InvoiceDetailScreen(
                    invoiceId = invoiceId,
                    onNavigateBack = { navController.popBackStack() },
                    onEditInvoice = { id ->
                        navController.navigate(Routes.INVOICE_CREATE)
                    }
                )
            }

            composable(
                route = Routes.PRODUCT_DETAILS,
                arguments = listOf(navArgument("productId") { type = NavType.LongType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
                ProductDetailsScreen(
                    productId = productId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Routes.ANALYZE) {
                AnalyzeScreen()
            }

            composable(Routes.SETTINGS) {
                SettingScreen(
                    onButtonClick = {
                        navController.navigate(Routes.INVOICES_LIST)
                    },
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme,
                    navController = navController,
                    invoiceListViewModel = sharedInvoiceListViewModel,
                    homeViewModel = sharedHomeViewModel
                )
            }

            composable(Routes.MAIN_PRODUCTS_LIST) {
                ServerProductListScreen()
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val navController = rememberNavController()
    ComposeTrainerTheme {
        MainScreen(
            navController = navController,
            isDarkTheme = false,
            onToggleTheme = {}
        )
    }
}
