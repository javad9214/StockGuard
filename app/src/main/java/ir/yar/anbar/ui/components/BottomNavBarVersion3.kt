package ir.yar.anbar.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import ir.yar.anbar.R
import ir.yar.anbar.ui.navigation.Routes

private data class NavBarItemV3(
    val label: String,
    val route: String,
    val iconRes: Int
)

@Composable
fun BottomNavBarVersion3(
    navController: NavController,
    currentRoute: String?,
    onFabClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavBarItemV3("Invoices", Routes.INVOICES_LIST, R.drawable.receipt_long_24px),
        NavBarItemV3("Analyze", Routes.ANALYZE, R.drawable.monitoring_24px),
        NavBarItemV3("Products", Routes.PRODUCTS_LIST, R.drawable.package_2_24px),
        NavBarItemV3("Home", Routes.HOME, R.drawable.home_24px)
    )

    Box(
        Modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // FAB
        FloatingActionButton(
            onClick = onFabClick,
            modifier = Modifier
                .offset(y = (-32).dp)
                .size(64.dp)
                .zIndex(1f),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 8.dp,
                pressedElevation = 12.dp
            ),
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add",
                modifier = Modifier.size(32.dp)
            )
        }

        Surface(
            modifier = modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .shadow(elevation = 14.dp, shape = RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 8.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .drawBehind {
                        // Draw custom cut-out path
                        val navBarWidth = size.width
                        val navBarHeight = size.height
                        val cutoutRadius = 40.dp.toPx()
                        val cutoutMargin = 10.dp.toPx()
                        val cutoutCenter = androidx.compose.ui.geometry.Offset(
                            navBarWidth / 2, cutoutMargin + cutoutRadius / 2
                        )
                        val path = Path().apply {
                            moveTo(0f, 0f)
                            lineTo(cutoutCenter.x - cutoutRadius, 0f)
                            arcTo(
                                rect = androidx.compose.ui.geometry.Rect(
                                    left = cutoutCenter.x - cutoutRadius,
                                    top = 0f - cutoutRadius + cutoutMargin,
                                    right = cutoutCenter.x + cutoutRadius,
                                    bottom = cutoutMargin + cutoutRadius
                                ),
                                startAngleDegrees = 180f,
                                sweepAngleDegrees = 180f,
                                forceMoveTo = false
                            )
                            lineTo(navBarWidth, 0f)
                            lineTo(navBarWidth, navBarHeight)
                            lineTo(0f, navBarHeight)
                            close()
                        }
                        drawPath(
                            path = path,
                            color = Color.Transparent // just for alpha cutout
                        )
                    },
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // First 2 items (left)
                    Row(
                        Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        items.take(2).forEach { item ->
                            BottomNavItemV3(
                                item = item,
                                isSelected = item.route == currentRoute,
                                onClick = {
                                    navigateToRoute(navController, item.route)
                                }
                            )
                        }
                    }
                    Spacer(Modifier.width(80.dp))
                    // Last 2 items (right)
                    Row(
                        Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        items.takeLast(2).forEach { item ->
                            BottomNavItemV3(
                                item = item,
                                isSelected = item.route == currentRoute,
                                onClick = {
                                    navigateToRoute(navController, item.route)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomNavItemV3(
    item: NavBarItemV3,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = tween(300), label = "nav_scale"
    )
    val indicatorHeight by animateDpAsState(
        targetValue = if (isSelected) 3.dp else 0.dp,
        animationSpec = tween(300), label = "nav_indicator"
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Icon(
            painter = painterResource(item.iconRes),
            contentDescription = item.label,
            tint = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier
                .scale(scale)
                .size(24.dp)
        )
        Spacer(Modifier.height(2.dp))
        Text(
            item.label,
            color = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            fontSize = 12.sp
        )
        Spacer(Modifier.height(2.dp))
        Box(
            Modifier
                .height(indicatorHeight)
                .width(18.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}

private fun navigateToRoute(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
