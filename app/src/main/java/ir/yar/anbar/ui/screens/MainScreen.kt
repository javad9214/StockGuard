package ir.yar.anbar.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import ir.yar.anbar.R
import ir.yar.anbar.ui.components.CustomNavigationBar
import ir.yar.anbar.ui.components.util.SnackyHost
import ir.yar.anbar.ui.components.util.rememberSnackyHostState
import ir.yar.anbar.ui.navigation.Routes
import ir.yar.anbar.ui.navigation.Screen
import ir.yar.anbar.ui.screens.home.HomeScreen
import ir.yar.anbar.ui.screens.invoice.invoicescreen.InvoiceScreen
import ir.yar.anbar.ui.screens.invoicelist.InvoiceDetailScreen
import ir.yar.anbar.ui.screens.invoicelist.InvoicesListScreen
import ir.yar.anbar.ui.screens.productlist.AddProduct
import ir.yar.anbar.ui.screens.productlist.ProductDetailsScreen
import ir.yar.anbar.ui.screens.productlist.ProductScreen
import ir.yar.anbar.ui.screens.productlist.serverlist.ServerProductListScreen
import ir.yar.anbar.ui.screens.setting.SettingScreen
import ir.yar.anbar.ui.screens.versionupdate.UpdateDialogContainer
import ir.yar.anbar.ui.theme.ComposeTrainerTheme
import ir.yar.anbar.ui.viewmodels.ProductsViewModel
import ir.yar.anbar.ui.viewmodels.home.HomeViewModel
import ir.yar.anbar.ui.viewmodels.versionupdate.VersionViewModel
import ir.yar.anbar.utils.str
import ir.yar.login.ui.screens.LoginScreen
import ir.yar.login.ui.screens.RegisterScreen
import ir.yar.login.ui.viewmodels.AuthViewModel

@Composable
fun MainScreen(
    navController: NavHostController,
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {}
) {

    // Create a shared HomeViewModel instance for barcode scanning
    val sharedHomeViewModel: HomeViewModel = hiltViewModel()
    val productsViewModel: ProductsViewModel = hiltViewModel()
    val versionViewModel: VersionViewModel = hiltViewModel()
    val authViewModel: AuthViewModel = hiltViewModel()
    // Check login status
    var isLoggedIn by remember { mutableStateOf<Boolean?>(null) }


    LaunchedEffect(Unit) {
        isLoggedIn = authViewModel.isLoggedIn()
    }

    // Show loading until we know the login status
    if (isLoggedIn == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // Auto check for updates when user is logged in
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn == true) {
            // Check only if it's been 24 hours since last check
            if (versionViewModel.shouldCheckForUpdates()) {
                versionViewModel.checkForUpdates(showDialogOnUpdate = true)
            }
        }
    }

    // Determine start destination based on login status
    val startDestination = if (isLoggedIn == true) Routes.HOME else Routes.LOGIN

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

    //for snackbar
    val scope = rememberCoroutineScope()
    val invoiceCompletedMessage = str(R.string.invoice_created_successfully)
    val snackyHostState = rememberSnackyHostState()

    Scaffold(
        bottomBar = {
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
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier
                    .padding(innerPadding)
            ) {
                composable(route = Screen.Login.route) {
                    LoginScreen(
                        onLoginSuccess = {
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        },
                        onRegisterClick = {
                            navController.navigate(Screen.Register.route)
                        }
                    )
                }

                composable(route = Screen.Register.route) {
                    RegisterScreen(
                        onNavigateToLogin = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Routes.PRODUCTS_LIST) {
                    ProductScreen(navController = navController)
                }

                composable(route = "product_create?barcode={barcode}&productId={productId}",
                    arguments = listOf(
                        navArgument("barcode") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        },
                        navArgument("productId") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        }
                    )){ backStackEntry ->

                    val barcode = backStackEntry.arguments?.getString("barcode")?.takeIf { it.isNotEmpty() }
                    val productIdString = backStackEntry.arguments?.getString("productId")
                    val productId = productIdString?.toLongOrNull()



                    AddProduct(
                        initialBarcode = barcode,
                        productId = productId,
                        onSave = { product ->
                            productsViewModel.addProduct(product)
                            navController.popBackStack()
                        },
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
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
                        navController = navController,
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
                        onNavigateBack = { navController.popBackStack() },
                        onLogout = {
                            // Logout and navigate to login
                            authViewModel.logout()
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(0) { inclusive = true } // Clear all back stack
                            }
                        }
                    )
                }

                composable(Routes.MAIN_PRODUCTS_LIST) {
                    ServerProductListScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }

            // Global SnackyHost - NOW it's outside NavHost but inside the Box!
            SnackyHost(hostState = snackyHostState)

            // Version Update Dialog - Shows automatically when update is available
            if (isLoggedIn == true) {
                UpdateDialogContainer(
                    viewModel = versionViewModel,
                    useCompactDialog = false // Set to true for compact dialog
                )
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
