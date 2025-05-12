package com.example.composetrainer.utils

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.composetrainer.R
import com.example.composetrainer.ui.navigation.BottomNavItem
import com.example.composetrainer.ui.navigation.Routes
import com.example.composetrainer.utils.dimen

@Composable
fun CustomNavigationBar(
    navController: NavController,
    currentRoute: String?,
    onFabClick: () -> Unit
) {
    val bottomNavItems = listOf(
        BottomNavItem("Invoices", Routes.INVOICES_LIST),
        BottomNavItem("Analyze", Routes.ANALYZE),
        BottomNavItem("Products", Routes.PRODUCTS_LIST),
        BottomNavItem("Home", Routes.HOME)
    )

    Box(
        contentAlignment = Alignment.BottomCenter
    ) {
        // Bottom Navigation Bar
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .shadow(8.dp)
                .zIndex(0f),
            containerColor = Color.White
        ) {
            // First half of the nav items
            bottomNavItems.take(2).forEach { item ->
                NavigationBarItem(
                    icon = {
                        NavBarItemIcon(
                            item = item,
                            isSelected = currentRoute == item.route
                        )
                    },
                    selected = currentRoute == item.route,
                    onClick = {
                        navigateToRoute(navController, item.route)
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = Color.Gray
                    )
                )
            }

            // Center space for FAB
            Spacer(modifier = Modifier.weight(1f))

            // Second half of the nav items
            bottomNavItems.takeLast(2).forEach { item ->
                NavigationBarItem(
                    icon = {
                        NavBarItemIcon(
                            item = item,
                            isSelected = currentRoute == item.route
                        )
                    },
                    selected = currentRoute == item.route,
                    onClick = {
                        navigateToRoute(navController, item.route)
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = Color.Gray
                    )
                )
            }
        }

        // Floating Action Button
        Card(
            modifier = Modifier
                .offset(y = (-40).dp)
                .zIndex(1f)
                .graphicsLayer {
                    shadowElevation = 0f
                    shape = CircleShape
                }
                .drawBehind {
                    val shadowColor = Color.Black.copy(alpha = 0.2f)
                    val shadowRadius = 2.dp.toPx()
                    val shadowOffsetY = -2.dp.toPx()
                    drawIntoCanvas {
                        val paint = Paint()
                        val frameworkPaint = paint.asFrameworkPaint()
                        frameworkPaint.color = shadowColor.toArgb()
                        frameworkPaint.setShadowLayer(
                            shadowRadius,
                            0f,
                            shadowOffsetY,
                            shadowColor.toArgb()
                        )
                        it.drawCircle(
                            center = Offset(size.width / 2, size.height / 2),
                            radius = size.minDimension / 2,
                            paint = paint
                        )
                    }
                },
            elevation = CardDefaults.cardElevation(0.dp),
            shape = CircleShape,
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            FloatingActionButton(
                onClick = onFabClick,
                modifier = Modifier.padding(dimen(R.dimen.space_2)),
                containerColor = MaterialTheme.colorScheme.primary,
                elevation = FloatingActionButtonDefaults.elevation(0.dp),
                shape = CircleShape
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.barcode_scanner_24px),
                    contentDescription = "Scan Barcode",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun NavBarItemIcon(
    item: BottomNavItem,
    isSelected: Boolean
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.height(44.dp)
    ) {
        Icon(
            painter = when (item.route) {
                Routes.INVOICES_LIST ->
                    painterResource(id = R.drawable.receipt_long_24px)

                Routes.ANALYZE ->
                    painterResource(id = R.drawable.monitoring_24px)

                Routes.PRODUCTS_LIST ->
                    painterResource(id = R.drawable.package_2_24px)

                Routes.HOME ->
                    painterResource(id = R.drawable.home_24px)

                else -> painterResource(id = R.drawable.home_24px)
            },
            contentDescription = item.title,
            modifier = Modifier
                .offset(
                    y = animateDpAsState(
                        targetValue = if (isSelected) (-4).dp else 0.dp,
                        animationSpec = tween(300)
                    ).value
                )
                .scale(
                    animateFloatAsState(
                        targetValue = if (isSelected) 1.2f else 1f,
                        animationSpec = tween(300)
                    ).value
                )
        )

        if (isSelected) {
            Box(
                modifier = Modifier
                    .height(2.dp)
                    .width(
                        animateDpAsState(
                            targetValue = 24.dp,
                            animationSpec = tween(300)
                        ).value
                    )
                    .offset(y = 16.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(1.dp)
                    )
            )
        }
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