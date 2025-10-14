package com.example.composetrainer.ui.screens.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.composetrainer.R
import com.example.composetrainer.ui.theme.Beirut_Medium
import com.example.composetrainer.utils.dateandtime.TimeRange
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.dimenTextSize
import com.example.composetrainer.utils.str

@Composable
fun DatePickerBottomDialog(
    modifier: Modifier = Modifier,
    selectedItem: TimeRange = TimeRange.TODAY,
    onNewSelected: (TimeRange) -> Unit
) {

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = dimen(R.dimen.space_1))
                .padding(horizontal = dimen(R.dimen.space_4)),
            horizontalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_2))
        ) {
            DatePickerItem(
                modifier = modifier.weight(1f),
                isSelected = selectedItem == TimeRange.TODAY,
                text = str(R.string.today),
                onClick = { onNewSelected(TimeRange.TODAY) },
            )
            DatePickerItem(
                modifier = modifier.weight(1f),
                isSelected = selectedItem == TimeRange.YESTERDAY,
                text = str(R.string.yesterday),
                onClick = { onNewSelected(TimeRange.YESTERDAY) },
            )
        }
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = dimen(R.dimen.space_1))
                .padding(horizontal = dimen(R.dimen.space_4)),
            horizontalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_2))
        ) {
            DatePickerItem(
                modifier = modifier.weight(1f),
                isSelected = selectedItem == TimeRange.THIS_WEEK,
                text = str(R.string.this_week),
                onClick = { onNewSelected(TimeRange.THIS_WEEK) },
            )
            DatePickerItem(
                modifier = modifier.weight(1f),
                isSelected = selectedItem == TimeRange.LAST_WEEK,
                text = str(R.string.last_week),
                onClick = { onNewSelected(TimeRange.LAST_WEEK) },
            )
        }
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = dimen(R.dimen.space_1))
                .padding(horizontal = dimen(R.dimen.space_4)),
            horizontalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_2))
        ) {
            DatePickerItem(
                modifier = modifier.weight(1f),
                isSelected = selectedItem == TimeRange.THIS_MONTH,
                text = str(R.string.this_month),
                onClick = { onNewSelected(TimeRange.THIS_MONTH) },
            )
            DatePickerItem(
                modifier = modifier.weight(1f),
                isSelected = selectedItem == TimeRange.LAST_MONTH,
                text = str(R.string.last_month),
                onClick = { onNewSelected(TimeRange.LAST_MONTH) },
            )
        }

        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = dimen(R.dimen.space_1))
                .padding(horizontal = dimen(R.dimen.space_4)),
            horizontalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_2))
        ) {
            DatePickerItem(
                modifier = modifier.weight(1f),
                isSelected = selectedItem == TimeRange.THIS_YEAR,
                text = str(R.string.this_year),
                onClick = { onNewSelected(TimeRange.THIS_YEAR) },
            )
            DatePickerItem(
                modifier = modifier.weight(1f),
                isSelected = selectedItem == TimeRange.LAST_YEAR,
                text = str(R.string.last_year),
                onClick = { onNewSelected(TimeRange.LAST_YEAR) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerItem(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean = false,
    onClick: () -> Unit,
) {
    CompositionLocalProvider(
        LocalRippleConfiguration provides RippleConfiguration(
            color = MaterialTheme.colorScheme.primary
        )
    ) {
        OutlinedButton(
            modifier = modifier,
            onClick = onClick,
            shape = RoundedCornerShape(dimen(R.dimen.radius_sm)), // Border radius
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    Color(0xFFE0E0E0),
                contentColor = if (isSelected)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.outline
            ),
            border = BorderStroke(
                dimen(R.dimen.stroke_dimen_sm),
                color = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = Beirut_Medium,
                    fontSize = dimenTextSize(R.dimen.text_size_lg)
                )

                if (isSelected) {

                    Spacer(modifier = Modifier.width(dimen(R.dimen.space_1)))

                    Icon(
                        painter = painterResource(id = R.drawable.check_24px),
                        contentDescription = "Selected",
                        modifier = Modifier.size(dimen(R.dimen.size_xs))
                    )

                }

            }

        }
    }

}