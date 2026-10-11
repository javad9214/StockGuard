package ir.yar.anbar.ui.components.customnavbars

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import ir.yar.anbar.R
import ir.yar.anbar.ui.navigation.BottomNavItem
import ir.yar.anbar.ui.navigation.Routes

@Composable
fun CustomNavigationBar(
    navController: NavController,
    currentRoute: String?,
    onFabClick: () -> Unit
) {
    val items = listOf(
        BottomNavItem(R.string.nav_invoices, Routes.INVOICES_LIST),
        BottomNavItem(R.string.nav_analyze, Routes.ANALYZE),
        BottomNavItem(R.string.nav_products, Routes.PRODUCTS_LIST),
        BottomNavItem(R.string.nav_home, Routes.HOME)
    )

    Box(contentAlignment = Alignment.TopCenter) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .height(68.dp),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                items.take(2).forEach { item ->
                    NavItem(item, currentRoute == item.route) {
                        navigateToRoute(navController, item.route)
                    }
                }

                Spacer(Modifier.width(56.dp)) // room for the FAB

                items.takeLast(2).forEach { item ->
                    NavItem(item, currentRoute == item.route) {
                        navigateToRoute(navController, item.route)
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onFabClick,
            modifier = Modifier
                .offset(y = (-6).dp)
                .size(60.dp)
                .border(4.dp, MaterialTheme.colorScheme.background, CircleShape),
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            elevation = FloatingActionButtonDefaults.elevation(6.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.barcode_scanner_24px),
                contentDescription = stringResource(R.string.scan_barcode)
            )
        }
    }
}

@Composable
private fun NavItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val background by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        label = "bg"
    )
    val tint by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.onPrimaryContainer
        else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "tint"
    )

    val title = stringResource(item.titleRes)

    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(background)
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(item.iconRes()),
            contentDescription = title,
            tint = tint
        )
        AnimatedVisibility(visible = selected) {
            Text(
                text = title,
                color = tint,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}

private fun BottomNavItem.iconRes(): Int = when (route) {
    Routes.INVOICES_LIST -> R.drawable.receipt_long_24px
    Routes.ANALYZE -> R.drawable.monitoring_24px
    Routes.PRODUCTS_LIST -> R.drawable.package_2_24px
    else -> R.drawable.home_24px
}

private fun navigateToRoute(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Preview(showBackground = true)
@Composable
fun CustomNavigationBarPreview() {
    CustomNavigationBar(
        navController = rememberNavController(),
        currentRoute = Routes.HOME,
        onFabClick = {}
    )
}