package com.example.composetrainer.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.composetrainer.R
import com.example.composetrainer.ui.navigation.Routes

private data class GlassBarNavItem(
    val label: String,
    val route: String,
    val iconRes: Int
)

@Composable
fun BottomNavBarVersion5(
    navController: NavController,
    onFabClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        GlassBarNavItem("Invoices", Routes.INVOICES_LIST, R.drawable.receipt_long_24px),
        GlassBarNavItem("Analyze", Routes.ANALYZE, R.drawable.monitoring_24px),
        GlassBarNavItem("Products", Routes.PRODUCTS_LIST, R.drawable.package_2_24px),
        GlassBarNavItem("Home", Routes.HOME, R.drawable.home_24px)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp)

    val glassBackgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.33f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
            Color.White.copy(alpha = 0.16f)
        )
    )
    val glassOverlayBrush = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.22f),
            Color.Transparent,
            Color.Transparent
        )
    )
    val glowingHaloBrush = Brush.radialGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.36f),
            Color.Transparent
        )
    )

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Frosted glass effect background with cut-out path
        Box(
            Modifier
                .fillMaxWidth()
                .height(84.dp)
                .zIndex(0f)
                .blur(28.dp) // BLUR for glass effect
                .drawBehind {
                    // Custom path for notch cut-out
                    val cutoutRadius = 38.dp.toPx()
                    val cutoutCenterX = size.width / 2f
                    val cutoutTop = 0f
                    val navBarHeight = size.height

                    val path = Path().apply {
                        val corner = 26.dp.toPx()
                        moveTo(0f, corner)
                        arcTo(
                            rect = Rect(0f, 0f, corner * 2, corner * 2),
                            startAngleDegrees = 180f, sweepAngleDegrees = 90f, forceMoveTo = false
                        )
                        lineTo(cutoutCenterX - cutoutRadius - 16.dp.toPx(), 0f)
                        arcTo(
                            rect = Rect(
                                cutoutCenterX - cutoutRadius,
                                cutoutTop,
                                cutoutCenterX + cutoutRadius,
                                cutoutRadius * 2
                            ),
                            startAngleDegrees = 180f, sweepAngleDegrees = -180f, forceMoveTo = false
                        )
                        lineTo(size.width - corner, 0f)
                        arcTo(
                            rect = Rect(size.width - corner * 2, 0f, size.width, corner * 2),
                            startAngleDegrees = 270f, sweepAngleDegrees = 90f, forceMoveTo = false
                        )
                        lineTo(size.width, navBarHeight)
                        lineTo(0f, navBarHeight)
                        close()
                    }
                    // Glass background gradient (precomputed)
                    drawPath(
                        path,
                        glassBackgroundBrush,
                        style = Fill,
                        alpha = 0.93f
                    )
                    // Soft white overlay for glass gleam
                    drawPath(
                        path,
                        glassOverlayBrush,
                        style = Fill,
                        alpha = 0.45f,
                        blendMode = BlendMode.Softlight
                    )
                }
                .shadow(
                    elevation = 30.dp,
                    shape = shape,
                    ambientColor = Color.White.copy(alpha = 0.10f)
                )
                .clip(shape)
        )
        // NAV ITEMS ROW
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 18.dp, vertical = 13.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.take(2).forEach { item ->
                GlassBarItem(
                    item = item,
                    isSelected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            Spacer(Modifier.width(76.dp))
            items.takeLast(2).forEach { item ->
                GlassBarItem(
                    item = item,
                    isSelected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
        // Floating "glowing orb" FAB
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-20).dp)
                .size(70.dp)
                .zIndex(1f)
                .shadow(
                    24.dp,
                    CircleShape,
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                )
                .drawBehind {
                    // Glowing halo overlaid beneath orb (precomputed brush)
                    drawCircle(
                        glowingHaloBrush,
                        radius = size.width * 0.53f,
                        center = center
                    )
                }
        ) {
            FloatingActionButton(
                onClick = onFabClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(58.dp),
                elevation = FloatingActionButtonDefaults.elevation(12.dp, 18.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

@Composable
private fun GlassBarItem(
    item: GlassBarNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        if (isSelected) 1.18f else 1.0f,
        animationSpec = tween(320)
    )

    val glowAlpha by animateFloatAsState(
        if (isSelected) 0.42f else 0f,
        animationSpec = tween(300)
    )

    val itemGlowColor = MaterialTheme.colorScheme.primary.copy(alpha = glowAlpha)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(68.dp)
            .padding(vertical = 2.dp)
            .drawBehind {
                if (isSelected) {
                    drawCircle(
                        color = itemGlowColor,
                        radius = size.minDimension * 0.66f,
                        center = center,
                        blendMode = BlendMode.Softlight
                    )
                }
            }
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
    ) {
        Icon(
            painter = painterResource(item.iconRes),
            contentDescription = item.label,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                alpha = 0.78f
            ),
            modifier = Modifier
                .size(26.dp)
                .scale(scale)
        )
        Spacer(Modifier.height(2.dp))
        Text(
            item.label,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                alpha = 0.82f
            ),
            fontSize = 12.sp
        )
    }
}

@Preview
@Composable
fun BottomNavBarVersion5Preview() {
    MaterialTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFa1c4fd).copy(alpha = 0.25f),
                            Color(0xFFc2e9fb).copy(alpha = 0.12f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            BottomNavBarVersion5(
                navController = rememberNavController(),
                onFabClick = {}
            )
        }
    }
}
