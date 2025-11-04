package com.example.composetrainer.ui.screens.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.composetrainer.R
import com.example.composetrainer.ui.theme.Beirut_Medium
import com.example.composetrainer.ui.theme.MRTPoster
import com.example.composetrainer.ui.theme.customError
import com.example.composetrainer.ui.theme.errorRed
import com.example.composetrainer.ui.theme.smoke_white
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.dimenTextSize
import com.example.composetrainer.utils.str

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoBarcodeFoundDialog(
    barcode: String,
    sheetState: SheetState,
    onAddToNewProductClicked: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null,
        shape = RoundedCornerShape(
            topStart = dimen(R.dimen.radius_lg),
            topEnd = dimen(R.dimen.radius_lg)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimen(R.dimen.size_xxs))
                    .clip(RoundedCornerShape(topStart = dimen(R.dimen.radius_lg), topEnd = dimen(R.dimen.radius_lg)))
                    .background(MaterialTheme.colorScheme.customError)
            )

            Spacer(modifier = Modifier.padding(vertical = dimen(R.dimen.space_3)))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .weight(2f)
                        .padding(start = dimen(R.dimen.space_1))
                        .size(dimen(R.dimen.size_lg)),
                    painter = painterResource(id = R.drawable.error_24px),
                    contentDescription = "Error Icon",
                    tint = MaterialTheme.colorScheme.customError,
                )

                Text(
                    modifier = Modifier
                        .weight(6f)
                        .padding(start = dimen(R.dimen.space_1), end = dimen(R.dimen.space_1)),
                    text = str(R.string.no_product_found_with_barcode),
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = MRTPoster,
                    fontSize = dimenTextSize(R.dimen.text_size_lg)
                )
            }

            Spacer(modifier = Modifier.padding(vertical = dimen(R.dimen.space_1)))

            Text(
                text = barcode,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = dimenTextSize(R.dimen.text_size_3xl),
                fontWeight = FontWeight.Medium,
                fontFamily = MRTPoster,
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterHorizontally)
                    .background(
                        color = smoke_white,
                        shape = RoundedCornerShape(dimen(R.dimen.radius_sm))
                    )
                    .padding(horizontal = dimen(R.dimen.space_4), vertical = dimen(R.dimen.space_2))
            )

            Spacer(modifier = Modifier.padding(vertical = dimen(R.dimen.space_3)))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimen(R.dimen.space_6)),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Button(
                    modifier = Modifier.weight(3f),
                    onClick = {
                        onAddToNewProductClicked()
                        onDismiss()
                    },
                    shape = RoundedCornerShape(dimen(R.dimen.radius_md))
                ) {
                    Text(
                        text = str(R.string.add_as_new_product),
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = Beirut_Medium,
                        fontSize = dimenTextSize(R.dimen.text_size_lg)
                    )
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { onDismiss() },
                    shape = RoundedCornerShape(dimen(R.dimen.radius_md)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(
                        text = str(R.string.cancel),
                        fontFamily = Beirut_Medium,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = dimenTextSize(R.dimen.text_size_lg),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

            }

            Spacer(modifier = Modifier.padding(vertical = dimen(R.dimen.space_2)))
        }
    }
}
