package com.example.composetrainer.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.example.composetrainer.R
import com.example.composetrainer.ui.navigation.Routes

data class NavItem(
    val title: String,
    val route: String
)

@Composable
fun CutOutNavBar(
    navController: NavController,
    currentRoute: String?,
    onFabClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        NavItem("Invoices", Routes.INVOICES_LIST),
        NavItem("Analyze", Routes.ANALYZE),
        NavItem("Products", Routes.PRODUCTS_LIST),
        NavItem("Home", Routes.HOME)
    )

    Box(
        modifier = modifier.fillMaxWidth(),
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
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                modifier = Modifier.size(32.dp)
            )
        }

        // Bottom Navigation Bar with cut-out
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(28.dp),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 4.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .drawBehind {
                        val navBarWidth = size.width
                        val navBarHeight = size.height
                        val cutoutRadius = 40.dp.toPx()
                        val cutoutMargin = 10.dp.toPx()

                        val cutoutCenter = Offset(navBarWidth / 2, cutoutMargin + cutoutRadius / 2)

                        val path = Path().apply {
                            // Start from the top-left corner
                            moveTo(0f, 0f)

                            // Line to the start of the cutout arc
                            lineTo(cutoutCenter.x - cutoutRadius, 0f)

                            // Draw the cutout arc (half-circle at the top)
                            arcTo(
                                androidx.compose.ui.geometry.Rect(
                                    left = cutoutCenter.x - cutoutRadius,
                                    top = 0f - cutoutRadius + cutoutMargin,
                                    right = cutoutCenter.x + cutoutRadius,
                                    bottom = cutoutMargin + cutoutRadius
                                ),
                                startAngleDegrees = 180f,
                                sweepAngleDegrees = 180f,
                                forceMoveTo = false
                            )

                            // Line to the top-right corner
                            lineTo(navBarWidth, 0f)

                            // Complete the rectangle
                            lineTo(navBarWidth, navBarHeight)
                            lineTo(0f, navBarHeight)
                            close()
                        }

                        drawPath(
                            path = path,
                            color = Color.Transparent,
                        )
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // First two nav items (left side)
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        navItems.take(2).forEach { item ->
                            NavItemComponent(
                                item = item,
                                isSelected = item.route == currentRoute,
                                onClick = {
                                    navigateToRoute(navController, item.route)
                                }
                            )
                        }
                    }

                    // Center space for FAB
                    Spacer(modifier = Modifier.width(80.dp))

                    // Last two nav items (right side)
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        navItems.takeLast(2).forEach { item ->
                            NavItemComponent(
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
private fun NavItemComponent(
    item: NavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.2f else 1f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "scale"
    )

    val indicatorHeight by animateDpAsState(
        targetValue = if (isSelected) 3.dp else 0.dp,
        animationSpec = tween(300),
        label = "indicator"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Icon(
            painter = when (item.route) {
                Routes.INVOICES_LIST -> painterResource(id = R.drawable.receipt_long_24px)
                Routes.ANALYZE -> painterResource(id = R.drawable.monitoring_24px)
                Routes.PRODUCTS_LIST -> painterResource(id = R.drawable.package_2_24px)
                Routes.HOME -> painterResource(id = R.drawable.home_24px)
                else -> painterResource(id = R.drawable.home_24px)
            },
            contentDescription = item.title,
            tint = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier
                .scale(scale)
                .size(24.dp)
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = item.title,
            color = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            textAlign = TextAlign.Center,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(2.dp))

        Box(
            modifier = Modifier
                .height(indicatorHeight)
                .width(20.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}

private fun navigateToRoute(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Preview(showBackground = true)
@Composable
fun CutOutNavBarPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(8.dp)
        ) {
            CutOutNavBar(
                navController = rememberNavController(),
                currentRoute = Routes.HOME,
                onFabClick = { }
            )
        }
    }
}
