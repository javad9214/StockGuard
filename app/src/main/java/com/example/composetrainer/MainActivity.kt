package com.example.composetrainer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composetrainer.ui.navigation.Screens
import com.example.composetrainer.ui.screens.HomeScreen
import com.example.composetrainer.ui.screens.LoginScreen
import com.example.composetrainer.ui.theme.ComposeTrainerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeTrainerTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = Screens.Login.route) {
        composable(Screens.Login.route) {
            LoginScreen(onLoginSuccess = {
                navController.navigate(Screens.Home.route){
                    popUpTo(Screens.Login.route){
                        inclusive = true
                    }
                }
            })
        }
        composable(Screens.Home.route) {
            HomeScreen(navController)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun GreetingPreview() {
    ComposeTrainerTheme {
        Greeting("Android")
    }
    Greeting("Seyed Mohammad Javad")
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}