package ir.yar.anbar.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun QuantityStepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    min: Int = 1,
    max: Int = Int.MAX_VALUE
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = { onValueChange((value - 1).coerceAtLeast(min)) },
            enabled = value > min
        ) {
            Icon(Icons.Default.Remove, "Decrease")
        }

        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        IconButton(
            onClick = { onValueChange((value + 1).coerceAtMost(max)) },
            enabled = value < max
        ) {
            Icon(Icons.Default.Add, "Increase")
        }
    }
}