package com.example.composetrainer.ui.navigation

sealed class Screens(val route: String) {
    object Home : Screens("home")
    object Login : Screens("login")
    object Register : Screens("register")
    object ForgotPassword : Screens("forgot_password")

}