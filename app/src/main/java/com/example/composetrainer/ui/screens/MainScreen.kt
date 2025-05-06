package com.example.composetrainer.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Propane
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.composetrainer.ui.navigation.BottomNavItem
import com.example.composetrainer.ui.navigation.Routes
import com.example.composetrainer.ui.screens.product.ProductScreen
import com.example.composetrainer.ui.screens.invoice.InvoiceScreen
import com.example.login.ui.screens.LoginScreen

@Composable
fun MainScreen(navController: NavHostController) {

    val bottomNavItems = listOf(
        BottomNavItem("Home", Routes.HOME, Icons.Default.Home),
        BottomNavItem("Product", Routes.PRODUCT, Icons.Default.Propane)
    )

    // Observe the current back stack entry
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold (
        bottomBar = {
            if (currentRoute != Routes.INVOICE) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = {
                                Icon(item.icon, contentDescription = item.title)
                            },
                            label = { Text(item.title) },
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
            }
        }
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