package com.example.composetrainer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composetrainer.ui.navigation.Routes
import com.example.composetrainer.ui.screens.HomeScreen
import com.example.composetrainer.ui.screens.product.ProductScreen
import com.example.login.ui.screens.LoginScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = Routes.LOGIN
            ) {
                composable(Routes.LOGIN) {
                    LoginScreen(
                        onLoginSuccess = {
                            navController.navigate(Routes.PRODUCT) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        }
                    )
                }
                composable(Routes.PRODUCT){
                    ProductScreen()
                }

                composable(Routes.HOME) {
                    HomeScreen()
                }
            }
        }
    }
}


