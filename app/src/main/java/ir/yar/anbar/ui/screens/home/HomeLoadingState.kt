package ir.yar.anbar.ui.screens.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import ir.yar.anbar.R
import ir.yar.anbar.ui.theme.ComposeTrainerTheme
import ir.yar.anbar.utils.dimen

/**
 * Skeleton placeholder shown while the first load of home data is in flight.
 * Mirrors the loaded layout (totals card + three product rows) so the
 * transition into real content causes minimal visual shift.
 */
@Composable
fun HomeLoadingState(modifier: Modifier = Modifier) {
    val skeletonColor = pulsingSkeletonColor()

    Column(modifier = modifier.fillMaxWidth()) {
        TotalsItemSkeleton(color = skeletonColor)

        Spacer(modifier = Modifier.height(dimen(R.dimen.space_4)))

        SectionTitleSkeleton(color = skeletonColor)
        Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))
        ProductCardsSkeleton(color = skeletonColor)

        Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))

        SectionTitleSkeleton(color = skeletonColor)
        Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))
        ProductCardsSkeleton(color = skeletonColor)

        Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))

        SectionTitleSkeleton(color = skeletonColor)
        Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))
        ProductCardsSkeleton(color = skeletonColor)
    }
}

@Composable
private fun TotalsItemSkeleton(color: Color) {
    ElevatedCard(
        modifier = Modifier
            .padding(dimen(R.dimen.space_4))
            .width(dimen(R.dimen.size_8xl)),
        shape = RoundedCornerShape(dimen(R.dimen.radius_lg)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(dimen(R.dimen.space_2))) {
            TotalsRowSkeleton(color = color)
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            TotalsRowSkeleton(color = color)
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            TotalsRowSkeleton(color = color)
        }
    }
}

@Composable
private fun TotalsRowSkeleton(color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimen(R.dimen.space_2)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SkeletonBox(
            modifier = Modifier
                .width(dimen(R.dimen.size_5xl))
                .height(dimen(R.dimen.space_5)),
            color = color
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_1))
        ) {
            SkeletonBox(
                modifier = Modifier
                    .width(dimen(R.dimen.size_3xl))
                    .height(dimen(R.dimen.space_5)),
                color = color
            )
            SkeletonBox(
                modifier = Modifier.size(dimen(R.dimen.size_xs)),
                color = color,
                shape = CircleShape
            )
        }
    }
}

@Composable
private fun SectionTitleSkeleton(color: Color) {
    SkeletonBox(
        modifier = Modifier
            .padding(start = dimen(R.dimen.space_4))
            .width(dimen(R.dimen.size_6xl))
            .height(dimen(R.dimen.space_5)),
        color = color
    )
}

@Composable
private fun ProductCardsSkeleton(color: Color) {
    // Horizontally scrollable like the real LazyRow of 320dp-wide cards
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        repeat(SKELETON_CARD_COUNT) {
            ProductCardSkeleton(color = color)
        }
    }
}

@Composable
private fun ProductCardSkeleton(color: Color) {
    ElevatedCard(
        modifier = Modifier
            .padding(dimen(R.dimen.space_1))
            .width(dimen(R.dimen.size_8xl)),
        shape = RoundedCornerShape(dimen(R.dimen.radius_lg)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(dimen(R.dimen.space_4))) {
            // Product name + rank badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                SkeletonBox(
                    modifier = Modifier
                        .weight(1f)
                        .height(dimen(R.dimen.space_5)),
                    color = color
                )
                Spacer(modifier = Modifier.width(dimen(R.dimen.space_2)))
                SkeletonBox(
                    modifier = Modifier
                        .width(dimen(R.dimen.size_3xl))
                        .height(dimen(R.dimen.space_4)),
                    color = color,
                    shape = RoundedCornerShape(dimen(R.dimen.radius_md))
                )
            }

            Spacer(modifier = Modifier.height(dimen(R.dimen.space_1)))

            // Sales metrics row
            SkeletonBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimen(R.dimen.size_xxl)),
                color = color,
                shape = RoundedCornerShape(dimen(R.dimen.radius_md))
            )

            Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))

            // Profit row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SkeletonBox(
                    modifier = Modifier
                        .width(dimen(R.dimen.size_4xl))
                        .height(dimen(R.dimen.space_4)),
                    color = color
                )
                SkeletonBox(
                    modifier = Modifier
                        .width(dimen(R.dimen.size_3xl))
                        .height(dimen(R.dimen.space_4)),
                    color = color
                )
            }

            Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))

            // Stock indicator row
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_2)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SkeletonBox(
                    modifier = Modifier.size(dimen(R.dimen.space_2)),
                    color = color,
                    shape = CircleShape
                )
                SkeletonBox(
                    modifier = Modifier
                        .width(dimen(R.dimen.size_3xl))
                        .height(dimen(R.dimen.space_4)),
                    color = color
                )
            }
        }
    }
}

@Composable
private fun SkeletonBox(
    modifier: Modifier = Modifier,
    color: Color,
    shape: Shape = RoundedCornerShape(dimen(R.dimen.radius_sm))
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(color)
    )
}

// One shared pulse so every placeholder breathes in sync
@Composable
private fun pulsingSkeletonColor(): Color {
    val transition = rememberInfiniteTransition(label = "skeletonPulse")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "skeletonAlpha"
    )
    return MaterialTheme.colorScheme.outlineVariant.copy(alpha = alpha)
}

private const val SKELETON_CARD_COUNT = 3

@Preview(showBackground = true)
@Composable
private fun HomeLoadingStatePreview() {
    ComposeTrainerTheme {
        HomeLoadingState()
    }
}
