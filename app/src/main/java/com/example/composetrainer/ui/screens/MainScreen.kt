package com.example.composetrainer.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.composetrainer.R
import com.example.composetrainer.ui.navigation.BottomNavItem
import com.example.composetrainer.ui.navigation.Routes
import com.example.composetrainer.ui.screens.product.ProductScreen
import com.example.composetrainer.ui.screens.invoice.InvoiceScreen
import com.example.login.ui.screens.LoginScreen

@Composable
fun MainScreen(navController: NavHostController) {

    val bottomNavItems = listOf(
        BottomNavItem("Invoices", Routes.INVOICE),
        BottomNavItem("Analyze", Routes.ANALYZE),
        BottomNavItem("Products", Routes.PRODUCT),
        BottomNavItem("Home", Routes.HOME)
    )

    // Observe the current back stack entry
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold (
        bottomBar = {
            Box(
                contentAlignment = Alignment.BottomCenter
            ) {
                // Bottom Navigation Bar
                NavigationBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .shadow(8.dp)
                        .zIndex(0f),
                    containerColor = Color.White
                ) {
                    // First half of the nav items
                    bottomNavItems.take(2).forEach { item ->
                        NavigationBarItem(
                            icon = {
                                when (item.route) {
                                    Routes.INVOICE -> Icon(
                                        painter = painterResource(id = R.drawable.receipt_long_24px),
                                        contentDescription = item.title
                                    )
                                    Routes.ANALYZE -> Icon(
                                        painter = painterResource(id = R.drawable.monitoring_24px),
                                        contentDescription = item.title
                                    )
                                    else -> Icon(
                                        painter = painterResource(id = R.drawable.home_24px),
                                        contentDescription = item.title
                                    )
                                }
                            },
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                    
                    // Center space for FAB
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Second half of the nav items
                    bottomNavItems.takeLast(2).forEach { item ->
                        NavigationBarItem(
                            icon = {
                                when (item.route) {
                                    Routes.PRODUCT -> Icon(
                                        painter = painterResource(id = R.drawable.package_2_24px),
                                        contentDescription = item.title
                                    )
                                    Routes.HOME -> Icon(
                                        painter = painterResource(id = R.drawable.home_24px),
                                        contentDescription = item.title
                                    )
                                    else -> Icon(
                                        painter = painterResource(id = R.drawable.home_24px),
                                        contentDescription = item.title
                                    )
                                }
                            },
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
                
                // Floating Action Button
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Routes.INVOICE) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier
                        .offset(y = (-32).dp)
                        .zIndex(1f),
                    containerColor = MaterialTheme.colorScheme.primary,
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.barcode_scanner_24px),
                        contentDescription = "Scan Barcode",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
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
            composable(Routes.PRODUCT){
                ProductScreen()
            }

            composable(Routes.HOME) {
                HomeScreen(onButtonClick = {
                    navController.navigate(Routes.INVOICE)
                })
            }

            composable(Routes.INVOICE) {
                InvoiceScreen(
                    onComplete = {
                        // Handle completion if needed
                    },
                    onClose = {
                        navController.popBackStack()
                    }
                )
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