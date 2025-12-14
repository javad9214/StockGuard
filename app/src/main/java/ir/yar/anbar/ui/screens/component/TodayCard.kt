package ir.yar.anbar.ui.screens.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import ir.yar.anbar.R
import ir.yar.anbar.ui.theme.Beirut_Medium
import ir.yar.anbar.utils.dimen
import ir.yar.anbar.utils.dimenTextSize
import ir.yar.anbar.utils.str

@Composable
fun TodayCard(
    modifier: Modifier = Modifier,
    onSelected: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = modifier
            .padding(dimen(R.dimen.space_2))
            .wrapContentHeight()
            .width(IntrinsicSize.Min)
            .animateContentSize(),
        shape = RoundedCornerShape(dimen(R.dimen.radius_xxl)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = { expanded = !expanded }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.wrapContentWidth()
        ) {
            TodayRow(
                text = str(R.string.today),
                isExpandedParent = expanded
            )

            if (expanded) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = dimen(R.dimen.space_4))
                )
                TodayRow(text = str(R.string.yesterday), isExpandedChild = true)
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = dimen(R.dimen.space_4))
                )
                TodayRow(text = str(R.string.this_week), isExpandedChild = true)
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = dimen(R.dimen.space_4))
                )
                TodayRow(text = str(R.string.last_week), isExpandedChild = true)
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = dimen(R.dimen.space_4))
                )
                TodayRow(text = str(R.string.this_month), isExpandedChild = true)
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = dimen(R.dimen.space_4))
                )
                TodayRow(text = str(R.string.last_month), isExpandedChild = true)
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = dimen(R.dimen.space_4))
                )
                TodayRow(text = str(R.string.this_year), isExpandedChild = true)
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = dimen(R.dimen.space_4))
                )
                TodayRow(text = str(R.string.last_year), isExpandedChild = true)

            }
        }
    }
}

@Composable
private fun TodayRow(
    text: String,
    isExpandedChild: Boolean = false,
    isExpandedParent: Boolean = false
) {
    Row(
        modifier = Modifier
            .wrapContentWidth()
            .height(dimen(R.dimen.size_lg))
            .padding(dimen(R.dimen.space_1)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(4f)
                .padding(end = dimen(R.dimen.space_1), start = dimen(R.dimen.space_4)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = Beirut_Medium,
                fontSize = dimenTextSize(R.dimen.text_size_lg)
            )
        }

        Spacer(modifier = Modifier.weight(0.5f))

        Box(
            modifier = Modifier
                .padding(end = dimen(R.dimen.space_1))
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (!isExpandedChild) {
                val iconRes = if (isExpandedParent) {
                    R.drawable.keyboard_arrow_up_24px
                } else {
                    R.drawable.keyboard_arrow_down_24px
                }

                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = if (isExpandedParent) "arrow up" else "arrow down"
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TodayCardPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.scrim),
            contentAlignment = Alignment.Center
        ) {
            TodayCard(
                modifier = Modifier,
                onSelected = {}
            )
        }
    }
}