package com.example.composetrainer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.composetrainer.R
import com.example.composetrainer.ui.navigation.BottomNavItem
import com.example.composetrainer.ui.navigation.Routes
import com.example.composetrainer.ui.screens.product.ProductScreen
import com.example.composetrainer.ui.screens.invoice.InvoiceScreen
import com.example.composetrainer.ui.screens.invoice.InvoicesListScreen
import com.example.composetrainer.utils.CustomNavigationBar
import com.example.composetrainer.utils.dimen
import com.example.login.ui.screens.LoginScreen
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween

@Composable
fun MainScreen(navController: NavHostController) {

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
            composable(Routes.PRODUCTS_LIST){
                ProductScreen()
            }

            composable(Routes.HOME) {
                HomeScreen(onButtonClick = {
                    navController.navigate(Routes.INVOICES_LIST)
                })
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
                // You'll need to create an InvoiceDetailScreen component
                Text("Invoice Details for $invoiceId")
            }

            composable(Routes.ANALYZE) {
                // Placeholder for Analyze screen
                Text(text = "Analyze Screen")
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    // Create a NavController for preview purposes.
    val navController = rememberNavController()
    MainScreen(navController = navController)
}