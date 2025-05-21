package com.example.composetrainer.ui.components

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.example.composetrainer.R
import com.example.composetrainer.ui.navigation.BottomNavItem
import com.example.composetrainer.ui.navigation.Routes
import com.example.composetrainer.utils.dimen

data class NavBarItem(val icon: ImageVector, val text: String, val route: String)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CustomNavigationBar(
    navController: NavController,
    currentRoute: String?,
    onFabClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary

    val navBarItems = listOf(
        NavBarItem(
            icon = Icons.Default.Add,
            text = "Invoices",
            route = Routes.INVOICES_LIST
        ),
        NavBarItem(
            icon = Icons.Default.Add,
            text = "Analyze",
            route = Routes.ANALYZE
        ),
        NavBarItem(
            icon = Icons.Default.Add,
            text = "Products",
            route = Routes.PRODUCTS_LIST
        ),
        NavBarItem(
            icon = Icons.Default.Add,
            text = "Home",
            route = Routes.HOME
        )
    )

    Box(
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                .shadow(elevation = 10.dp, shape = RoundedCornerShape(30.dp))
                .drawBehind {
                    val navBarHeight = 64.dp.toPx()
                    val cutoutWidth = 72.dp.toPx()
                    val cutoutHeight = 64.dp.toPx()
                    val cornerRadius = 36.dp.toPx()

                    val left = center.x - cutoutWidth / 2
                    val top = -cutoutHeight / 2
                    val right = center.x + cutoutWidth / 2
                    val bottom = top + cutoutHeight

                    val path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(left - cornerRadius, 0f)

                        cubicTo(
                            left, 0f,
                            left, 0f,
                            left, cornerRadius
                        )

                        lineTo(left, bottom - cornerRadius)

                        cubicTo(
                            left, bottom,
                            left, bottom,
                            left + cornerRadius, bottom
                        )

                        lineTo(right - cornerRadius, bottom)

                        cubicTo(
                            right, bottom,
                            right, bottom,
                            right, bottom - cornerRadius
                        )

                        lineTo(right, cornerRadius)

                        cubicTo(
                            right, 0f,
                            right, 0f,
                            right + cornerRadius, 0f
                        )

                        lineTo(size.width, 0f)
                        lineTo(size.width, navBarHeight)
                        lineTo(0f, navBarHeight)
                        close()
                    }

                    drawPath(path = path, color = primary, style = Fill)
                }
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.weight(1f)
            ) {
                navBarItems.take(2).forEach { item ->
                    NavBarItemColumn(
                        item = item,
                        isSelected = currentRoute == item.route,
                        onClick = {
                            navigateToRoute(navController, item.route)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.width(72.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.weight(1f)
            ) {
                navBarItems.takeLast(2).forEach { item ->
                    NavBarItemColumn(
                        item = item,
                        isSelected = currentRoute == item.route,
                        onClick = {
                            navigateToRoute(navController, item.route)
                        }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = onFabClick,
            containerColor = Color.White,
            contentColor = primary,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 6.dp,
                pressedElevation = 8.dp
            ),
            modifier = Modifier
                .offset(y = (-75).dp)
                .zIndex(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = primary
            )
        }
    }
}

@Composable
private fun NavBarItemColumn(
    item: NavBarItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
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
            contentDescription = item.text,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .scale(
                    animateFloatAsState(
                        targetValue = if (isSelected) 1.2f else 1f,
                        animationSpec = tween(300)
                    ).value
                )
        )

        Text(
            text = item.text,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 12.sp
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
fun PreviewCustomNavigationBar() {
    val navController = rememberNavController()
    MaterialTheme {
        Box(modifier = Modifier.padding(bottom = 16.dp)) {
            CustomNavigationBar(
                navController = navController,
                currentRoute = Routes.HOME,
                onFabClick = { /* Handle FAB click here */ }
            )
        }
    }
}