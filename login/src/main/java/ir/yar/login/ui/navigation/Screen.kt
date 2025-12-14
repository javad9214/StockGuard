package ir.yar.login.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen(Routes.LOGIN)
    object Register : Screen(Routes.REGISTER)
}