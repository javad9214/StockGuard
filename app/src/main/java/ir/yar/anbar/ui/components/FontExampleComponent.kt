package ir.yar.anbar.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.yar.anbar.ui.theme.AppFont
import ir.yar.anbar.ui.theme.ComposeTrainerTheme
import ir.yar.anbar.ui.theme.bold

/**
 * A component that demonstrates different ways to use custom fonts in the app
 */
@Composable
fun FontExampleComponent() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Using MaterialTheme Typography
            Text(
                text = "عنوان بزرگ با کد اک",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Using AppFont utility with direct fontFamily
            Text(
                text = "متن بدنه با خانواده فونت زر",
                fontFamily = AppFont.Zar,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Using semantic fonts
            Text(
                text = "عنوان با فونت سمنتیک",
                fontFamily = AppFont.Header,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Using extension function for bold font
            val (fontFamily, fontWeight) = AppFont.BNazanin.bold()
            Text(
                text = "متن کوچک با بی نازنین",
                fontFamily = fontFamily,
                fontWeight = fontWeight
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Using different font styles
            Text(
                text = "متن بزرگ با بی کامپس",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FontExampleComponentPreview() {
    ComposeTrainerTheme {
        FontExampleComponent()
    }
}