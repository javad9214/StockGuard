package ir.yar.anbar.ui.components.util

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ir.yar.anbar.R
import ir.yar.anbar.utils.dimen

// Menu Item Data Class
data class BottomSheetMenuItem(
    val text: String,
    val icon: Painter,
    val iconTint: Color? = null,
    val textColor: Color? = null,
    val backgroundColor: Color? = null,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetMenu(
    visible: Boolean,
    onDismiss: () -> Unit,
    items: List<BottomSheetMenuItem>,
    fontFamily: FontFamily? = null,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (visible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = modifier
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                items.forEachIndexed { index, item ->
                    BottomSheetMenuItemView(
                        item = item,
                        fontFamily = fontFamily,
                        isLast = index == items.lastIndex
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomSheetMenuItemView(
    item: BottomSheetMenuItem,
    fontFamily: FontFamily?,
    isLast: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (item.backgroundColor != null) {
                    Modifier.background(item.backgroundColor)
                } else {
                    Modifier
                }
            )
            .clickable { item.onClick() }
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.text,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                textAlign = TextAlign.End,
                fontFamily = fontFamily,
                color = item.textColor ?: MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.width(dimen(R.dimen.space_4)))

            Icon(
                painter = item.icon,
                contentDescription = item.text,
                tint = item.iconTint ?: MaterialTheme.colorScheme.onSurface
            )
        }
    }

    // Add divider between items except for the last one
    if (!isLast && item.backgroundColor == null) {
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 24.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

// Example Usage for your product menu
@Composable
fun ProductMenuExample(
    product: Product, // Your product data class
    showMenu: Boolean,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDisable: () -> Unit,
    onDelete: () -> Unit,
    myFontFamily: FontFamily
) {
    BottomSheetMenu(
        visible = showMenu,
        onDismiss = onDismiss,
        fontFamily = myFontFamily,
        items = listOf(
            BottomSheetMenuItem(
                text = "Edit", // str(R.string.edit)
                icon = painterResource(id = R.drawable.edit_24px),
                onClick = {
                    onDismiss()
                    onEdit()
                }
            ),
            BottomSheetMenuItem(
                text = if (product.isActive) "Disable" else "Enable", // str(R.string.disable/enable)
                icon = painterResource(id = R.drawable.block_24px),
                onClick = {
                    onDismiss()
                    onDisable()
                }
            ),
            BottomSheetMenuItem(
                text = "Delete", // str(R.string.delete)
                icon = painterResource(id = R.drawable.delete_24px),
                iconTint = Color.Red,
                textColor = Color.Red,
                onClick = {
                    onDismiss()
                    onDelete()
                }
            )
        )
    )
}

// Dummy Product class for example (replace with your actual Product class)
data class Product(
    val isActive: Boolean = true
)