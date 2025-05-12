package com.example.composetrainer.ui.screens.invoice.invoicescreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.composetrainer.ui.screens.invoice.invoicescreen.InvoiceScreen
import com.example.composetrainer.ui.theme.ComposeTrainerTheme

@Preview(
    name = "Invoice Screen Preview",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun InvoiceScreenPreview() {
    ComposeTrainerTheme {
        InvoiceScreen(
            onComplete = {},
            onClose = {}
        )
    }
}