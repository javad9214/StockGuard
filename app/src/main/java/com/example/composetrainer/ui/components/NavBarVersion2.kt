package com.example.composetrainer.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.composetrainer.R
import com.example.composetrainer.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun BottomNavWithFAB() {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf(
        BottomNavItem(
            title = "Home",
            route = "home",
            icon = Icons.Filled.Home
        ),
        BottomNavItem(
            title = "Search",
            route = "search",
            icon = Icons.Filled.Search
        ),
        BottomNavItem(
            title = "Notifications",
            route = "notifications",
            icon = Icons.Filled.Notifications
        ),
        BottomNavItem(
            title = "Profile",
            route = "profile",
            icon = Icons.Filled.Person
        )
    )

    Scaffold(
        bottomBar = {
            // BottomAppBar with cutout for FAB
            BottomAppBar(
                modifier = Modifier
                    .height(80.dp)
                    .clip(RoundedCornerShape(16.dp)),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                actions = {
                    // First two items on the left
                    items.take(2).forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = item.icon!!,
                                    contentDescription = item.title
                                )
                            },
                            label = { Text(item.title) },
                            selected = selectedItem == index,
                            onClick = { selectedItem = index },
                            alwaysShowLabel = false
                        )
                    }

                    // Spacer for FAB
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                    )

                    // Last two items on the right
                    items.takeLast(2).forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = item.icon!!,
                                    contentDescription = item.title
                                )
                            },
                            label = { Text(item.title) },
                            selected = selectedItem == index + 2,
                            onClick = { selectedItem = index + 2 },
                            alwaysShowLabel = false
                        )
                    }
                },
                floatingActionButton = {
                    // FAB that appears in the cutout
                    FloatingActionButton(
                        onClick = { /* Handle FAB click */ },
                        containerColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .offset(y = (-20).dp)
                            .shadow(
                                elevation = 6.dp,
                                shape = CircleShape,
                                spotColor = MaterialTheme.colorScheme.primary
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add!!,
                            contentDescription = "Add",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Selected tab: ${items[selectedItem].title}")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavBarVersion2(
    navController: NavController,
    currentRoute: String?,
    onFabClick: () -> Unit
) {
    val items = listOf(
        BottomNavItem(
            title = "Invoices",
            route = Routes.INVOICES_LIST
        ),
        BottomNavItem(
            title = "Analyze",
            route = Routes.ANALYZE
        ),
        BottomNavItem(
            title = "Products",
            route = Routes.PRODUCTS_LIST
        ),
        BottomNavItem(
            title = "Home",
            route = Routes.HOME
        )
    )

    // BottomAppBar with cutout for FAB
    BottomAppBar(
        modifier = Modifier
            .height(80.dp)
            .clip(RoundedCornerShape(16.dp)),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        actions = {
            // First two items on the left
            items.take(2).forEachIndexed { index, item ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = when (item.route) {
                                Routes.INVOICES_LIST -> painterResource(id = R.drawable.receipt_long_24px)
                                Routes.ANALYZE -> painterResource(id = R.drawable.monitoring_24px)
                                Routes.PRODUCTS_LIST -> painterResource(id = R.drawable.package_2_24px)
                                Routes.HOME -> painterResource(id = R.drawable.home_24px)
                                else -> painterResource(id = R.drawable.home_24px)
                            },
                            contentDescription = item.title
                        )
                    },
                    label = { Text(item.title) },
                    selected = item.route == currentRoute,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    alwaysShowLabel = false
                )
            }

            // Spacer for FAB
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
            )

            // Last two items on the right
            items.takeLast(2).forEachIndexed { index, item ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = when (item.route) {
                                Routes.INVOICES_LIST -> painterResource(id = R.drawable.receipt_long_24px)
                                Routes.ANALYZE -> painterResource(id = R.drawable.monitoring_24px)
                                Routes.PRODUCTS_LIST -> painterResource(id = R.drawable.package_2_24px)
                                Routes.HOME -> painterResource(id = R.drawable.home_24px)
                                else -> painterResource(id = R.drawable.home_24px)
                            },
                            contentDescription = item.title
                        )
                    },
                    label = { Text(item.title) },
                    selected = item.route == currentRoute,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    alwaysShowLabel = false
                )
            }
        },
        floatingActionButton = {
            // FAB that appears in the cutout
            FloatingActionButton(
                onClick = onFabClick,
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .offset(y = (-20).dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = CircleShape,
                        spotColor = MaterialTheme.colorScheme.primary
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.Add!!,
                    contentDescription = "Add",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    )
}

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector? = null
)

@Preview(showBackground = true)
@Composable
fun NavBarVersion2Preview() {
    MaterialTheme {
        NavBarVersion2(
            navController = rememberNavController(),
            currentRoute = Routes.HOME,
            onFabClick = {}
        )
    }
}
