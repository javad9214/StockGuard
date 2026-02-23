package ir.yar.anbar.ui.components


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ir.yar.anbar.R
import ir.yar.anbar.ui.navigation.Routes

private data class NavItemV4(
    val label: String,
    val route: String,
    val iconRes: Int
)

@Composable
fun BottomNavBarVersion4(
    navController: NavController,
    onFabClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItemV4("Invoices", Routes.INVOICES_LIST, R.drawable.receipt_long_24px),
        NavItemV4("Analyze", Routes.ANALYZE, R.drawable.monitoring_24px),
        NavItemV4("Products", Routes.PRODUCTS_LIST, R.drawable.package_2_24px),
        NavItemV4("Home", Routes.HOME, R.drawable.home_24px)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Background surface with curved top and notch
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .drawBehind {
                    val width = size.width
                    val height = size.height
                    val fabRadius = 30.dp.toPx()
                    val curveHeight = 20.dp.toPx()

                    val path = Path().apply {
                        moveTo(0f, height) // Start bottom left
                        lineTo(0f, curveHeight) // Top left

                        // Left curve to FAB notch
                        cubicTo(
                            width * 0.25f, curveHeight,
                            width * 0.4f, 0f,
                            width * 0.5f - fabRadius, 0f
                        )

                        // FAB notch (half-circle)
                        arcTo(
                            rect = androidx.compose.ui.geometry.Rect(
                                left = width * 0.5f - fabRadius,
                                top = 0f,
                                right = width * 0.5f + fabRadius,
                                bottom = fabRadius * 2
                            ),
                            startAngleDegrees = 180f,
                            sweepAngleDegrees = 180f,
                            forceMoveTo = false
                        )

                        // Right curve from FAB notch
                        cubicTo(
                            width * 0.6f, 0f,
                            width * 0.75f, curveHeight,
                            width, curveHeight
                        )

                        lineTo(width, height) // Bottom right
                        close()
                    }
                    drawPath(path, color = Color.Transparent)
                },
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = 0.dp,
                bottomEnd = 0.dp
            ),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            shadowElevation = 12.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items.take(2).forEach { item ->
                        NavItemV4(
                            item = item,
                            isSelected = item.route == currentRoute,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.width(72.dp))

                    items.takeLast(2).forEach { item ->
                        NavItemV4(
                            item = item,
                            isSelected = item.route == currentRoute,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        }

        // FAB centered in the notch
        FloatingActionButton(
            onClick = onFabClick,
            modifier = Modifier
                .offset(y = (-30).dp)
                .zIndex(1f)
                .shadow(
                    elevation = 10.dp,
                    shape = CircleShape,
                    clip = true
                ),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun NavItemV4(
    item: NavItemV4,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val animationProgress by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.7f,
        animationSpec = tween(300)
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(80.dp)
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Icon(
            painter = painterResource(item.iconRes),
            contentDescription = item.label,
            tint = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            },
            modifier = Modifier
                .size(24.dp)
                .scale(animationProgress)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = item.label,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            },
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Preview
@Composable
fun BottomNavBarVersion4Preview() {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            BottomNavBarVersion4(
                navController = rememberNavController(),
                onFabClick = {}
            )
        }
    }
}
