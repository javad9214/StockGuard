package com.example.composetrainer.ui.screens.invoice

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composetrainer.R
import com.example.composetrainer.ui.theme.ComposeTrainerTheme
import com.example.composetrainer.ui.viewmodels.InvoiceViewModel
import com.example.composetrainer.utils.DateFormatter
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.dimenTextSize
import com.example.composetrainer.utils.str

@Composable
fun InvoiceScreen(
    onComplete: () -> Unit,
    onClose: () -> Unit,
    viewModel: InvoiceViewModel = hiltViewModel()
) {
    val persianDate = remember { DateFormatter.getHijriShamsiDate() }
    val currentTime = remember { DateFormatter.getCurrentTimeFormatted() }
    val nextInvoiceNumber by viewModel.nextInvoiceNumber.collectAsState()
    var showProductSelection by remember { mutableStateOf(false) }
    val products by viewModel.products.collectAsState()

    val view = LocalView.current
    val context = LocalContext.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (context as Activity).window
            window.statusBarColor = ContextCompat.getColor(context, R.color.white)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.getNextInvoiceNumberId()
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(
            bottomStart = dimen(R.dimen.radius_xl),
            bottomEnd = dimen(R.dimen.radius_xl)
        ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp,
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimen(R.dimen.space_1)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                    )
                }

                Text(
                    text = nextInvoiceNumber?.toString() ?: "...",
                    fontSize = dimenTextSize(R.dimen.text_size_md),
                    modifier = Modifier.padding(start = dimen(R.dimen.space_2))
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = str(R.string.sale_invoice),
                    fontSize = dimenTextSize(R.dimen.text_size_xl),
                    textAlign = TextAlign.End,
                    modifier = Modifier.padding(end = dimen(R.dimen.space_4))
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = dimen(R.dimen.space_4),
                        vertical = dimen(R.dimen.space_4)
                    )
                    .height(IntrinsicSize.Min)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Date",
                            modifier = Modifier.padding(end = dimen(R.dimen.space_1))
                        )
                        Text(text = persianDate)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = dimen(R.dimen.space_2))
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Time",
                            modifier = Modifier.padding(end = dimen(R.dimen.space_1))
                        )
                        Text(text = currentTime)
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Text(text = str(R.string.buyer))
                        Text(text = str(R.string.unknown))
                    }
                    Icon(
                        painter = painterResource(id = R.drawable.face_24px),
                        contentDescription = "User",
                        modifier = Modifier
                            .padding(start = dimen(R.dimen.space_2))
                            .fillMaxHeight()
                            .aspectRatio(1f)
                    )
                }
            }
            // Add product button
            Button(
                onClick = { showProductSelection = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = dimen(R.dimen.space_4),
                        vertical = dimen(R.dimen.space_2)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Product",
                    modifier = Modifier.padding(end = dimen(R.dimen.space_2))
                )
                Text(text = str(R.string.add_product))
            }
        }
    }
    if (showProductSelection) {
        ProductSelectionBottomSheet(
            products = products,
            onAddToInvoice = { product, quantity ->
                viewModel.addToCurrentInvoice(product, quantity)
            },
            onDismiss = { showProductSelection = false }
        )
    }
}

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