package com.example.composetrainer.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.composetrainer.ui.navigation.BottomNavItem
import com.example.composetrainer.ui.navigation.Routes
import com.example.composetrainer.ui.screens.productlist.ProductScreen
import com.example.composetrainer.ui.screens.invoice.invoicescreen.InvoiceScreen
import com.example.composetrainer.ui.screens.invoicelist.InvoicesListScreen
import com.example.composetrainer.ui.screens.invoicelist.InvoiceDetailScreen
import com.example.composetrainer.ui.theme.ComposeTrainerTheme
import com.example.composetrainer.ui.components.CustomNavigationBar
import com.example.login.ui.screens.LoginScreen

@Composable
fun MainScreen(
    navController: NavHostController,
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {}
) {

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

    Scaffold (
        bottomBar = {
            if (shouldShowBottomNav) {
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
        },
    ){ innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN,
            modifier = Modifier.padding(innerPadding)
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
                ProductScreen()
            }

            composable(Routes.HOME) {
                HomeScreen(
                    onButtonClick = {
                        navController.navigate(Routes.INVOICES_LIST)
                    },
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme
                )
            }

            composable(Routes.INVOICES_LIST) {
                InvoicesListScreen(
                    onCreateNew = {
                        navController.navigate(Routes.INVOICE_CREATE)
                    },
                    onInvoiceClick = { invoiceId ->
                        navController.navigate(Routes.INVOICE_DETAILS.replace("{invoiceId}", invoiceId.toString()))
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
                    }
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

            composable(Routes.ANALYZE) {
                Text(text = "Analyze Screen")
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