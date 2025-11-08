package com.example.composetrainer.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen(Routes.LOGIN)
    object Register : Screen(Routes.REGISTER)
    object Home : Screen(Routes.HOME)
    object Products : Screen(Routes.PRODUCTS_LIST)
    object ProductCreate {
        const val route = "product_create?barcode={barcode}&productId={productId}"

        fun createRoute(barcode: String? = null, productId: Long? = null): String {
            return "product_create?barcode=${barcode ?: ""}&productId=${productId ?: ""}"
        }
    }
    object Invoice : Screen(Routes.INVOICE_CREATE)
    object InvoicesList : Screen(Routes.INVOICES_LIST)
    object InvoiceDetails : Screen(Routes.INVOICE_DETAILS) {
        fun createRoute(invoiceId: Long): String = "invoice_details/$invoiceId"
    }
    object ProductDetails : Screen(Routes.PRODUCT_DETAILS) {
        fun createRoute(productId: Long): String = "product_details/$productId"
    }
    object MainServerProductLiat: Screen(Routes.MAIN_PRODUCTS_LIST)
    object Analyze : Screen(Routes.ANALYZE)
    object Settings : Screen(Routes.SETTINGS)
}
