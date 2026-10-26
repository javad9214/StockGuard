package ir.yar.anbar.ui.components.customnavbars

import ir.yar.anbar.ui.theme.Vazir
import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import ir.yar.anbar.R
import ir.yar.anbar.ui.navigation.BottomNavItem
import ir.yar.anbar.ui.navigation.Routes
import ir.yar.anbar.ui.theme.ComposeTrainerTheme
import kotlin.math.PI
import kotlin.math.sin

// Bar geometry: the dock is a floating rounded bar with a circular cradle notch
// cut into its top edge; the scan FAB sits centered in that notch.
private val BarHeight = 66.dp
private val BarCorner = 26.dp
private val FabSize = 58.dp
private val CradleRadius = 33.dp        // FabSize / 2 + 4dp ring gap
private val CradleTopClearance = 34.dp  // card top offset inside the host box
private val BarBottomMargin = 12.dp

// Nav item geometry
private val NavIconSize = 28.dp         // larger icon, also used when unselected
private val SelectedLift = 9.dp         // how far icon + pill rise to make room for the label

private val PillShape = RoundedCornerShape(15.dp)

private val NavItems = listOf(
    BottomNavItem(R.string.nav_invoices, Routes.INVOICES_LIST),
    BottomNavItem(R.string.nav_analyze, Routes.ANALYZE),
    BottomNavItem(R.string.nav_products, Routes.PRODUCTS_LIST),
    BottomNavItem(R.string.nav_home, Routes.HOME)
)

private val BouncySpring = spring<Float>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessMedium
)

private val BoldSpring = spring<Float>(
    dampingRatio = Spring.DampingRatioMediumBouncy,   // more overshoot
    stiffness = Spring.StiffnessMediumLow          // a bit slower, so the bounce is visible
)

/**
 * Vertical space the floating bar occludes at the bottom of the screen,
 * including the system navigation-bar inset. Scrollable screens use this as
 * bottom content padding so their last items can be scrolled clear of the bar.
 */
@Composable
fun navigationBarOverlayBottomPadding(): Dp =
    WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() +
            (CradleTopClearance + BarHeight + BarBottomMargin)

@Composable
fun ModernNavigationBar(
    navController: NavController,
    currentRoute: String?,
    onFabClick: () -> Unit
) {
    val view = LocalView.current
    val barShape = remember { CradleBarShape(BarCorner, CradleRadius) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(start = 16.dp, end = 16.dp, bottom = BarBottomMargin)
            .height(BarHeight + CradleTopClearance),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = CradleTopClearance)
                .height(BarHeight)
                .shadow(
                    elevation = 14.dp,
                    shape = barShape,
                    clip = true,
                    ambientColor = Color.Black,
                    spotColor = Color.Black
                )
                .background(MaterialTheme.colorScheme.surface, barShape)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f),
                    shape = barShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavItemsGroup(NavItems.take(2), currentRoute, navController)
                Spacer(Modifier.width(CradleRadius * 2 + 2.dp))
                NavItemsGroup(NavItems.takeLast(2), currentRoute, navController)
            }
        }

        ScanFab(
            modifier = Modifier.offset(y = CradleTopClearance - FabSize / 2),
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onFabClick()
            }
        )
    }
}

@Composable
private fun RowScope.NavItemsGroup(
    items: List<BottomNavItem>,
    currentRoute: String?,
    navController: NavController
) {
    items.forEach { item ->
        val selected = currentRoute == item.route
        NavItem(
            modifier = Modifier.weight(1f),
            item = item,
            isSelected = selected,
            onClick = { if (!selected) navigateToRoute(navController, item.route) }
        )
    }
}

@Composable
private fun ScanFab(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = BouncySpring,
        label = "fabScale"
    )

    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
            .size(FabSize)
            // State is read in the draw phase -> no recomposition per frame.
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape),
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 8.dp,
            pressedElevation = 10.dp
        ),
        interactionSource = interactionSource
    ) {
        Icon(
            painter = painterResource(R.drawable.barcode_scanner_24px),
            contentDescription = stringResource(R.string.scan_barcode)
        )
    }
}

@Composable
private fun NavItem(
    modifier: Modifier = Modifier,
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val view = LocalView.current
    val title = stringResource(item.titleRes)
    val pillColor = MaterialTheme.colorScheme.primaryContainer

    val iconTint by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
        else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(200),
        label = "iconTint"
    )

    val progress by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = BoldSpring,
        label = "selection"
    )

    // The whole item is one box: unselected -> icon centered vertically,
    // selected -> icon + pill lift up and the label appears at the bottom.
    Box(
        modifier = modifier
            .fillMaxSize()
            .selectable(
                selected = isSelected,
                role = Role.Tab,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        // Pill + icon move together
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.graphicsLayer {
                translationY = -SelectedLift.toPx() * progress
            }
        ) {
            // Pill: stretches in from the sides and overshoots
            Box(
                modifier = Modifier
                    .size(width = 56.dp, height = 32.dp)
                    .graphicsLayer {
                        scaleX = lerp(0.2f, 1f, progress)
                        scaleY = lerp(0.5f, 1f, progress)
                        alpha = progress.coerceIn(0f, 1f)
                    }
                    .background(pillColor, PillShape)
            )
            // Icon: big when idle, pops + tilts when selected
            Icon(
                painter = painterResource(item.iconRes()),
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier
                    .size(NavIconSize)
                    .graphicsLayer {
                        val s = lerp(1f, 1.05f, progress)
                        scaleX = s
                        scaleY = s
                        rotationZ = sin(progress * PI.toFloat()) * 12f
                    }
            )
        }

        // Label: slides up while fading in
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelSmall,
            fontFamily = Vazir,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 6.dp)
                .clearAndSetSemantics { }
                .graphicsLayer {
                    alpha = progress.coerceIn(0f, 1f)
                    translationY = (1f - progress) * 10.dp.toPx()
                }
        )
    }
}

/**
 * A rounded bar with a circular cradle notch cut into the top edge,
 * centered horizontally, so a FAB can dock into it.
 */
private class CradleBarShape(
    private val barCorner: Dp,
    private val cradleRadius: Dp
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val corner = with(density) { barCorner.toPx() }
        val cradle = with(density) { cradleRadius.toPx() }
        val w = size.width
        val h = size.height
        val cx = w / 2f

        val path = Path().apply {
            moveTo(corner, 0f)
            lineTo(cx - cradle, 0f)
            arcTo(
                rect = Rect(cx - cradle, -cradle, cx + cradle, cradle),
                startAngleDegrees = 180f,
                sweepAngleDegrees = -180f,
                forceMoveTo = false
            )
            lineTo(w - corner, 0f)
            arcTo(Rect(w - 2 * corner, 0f, w, 2 * corner), 270f, 90f, false)
            lineTo(w, h - corner)
            arcTo(Rect(w - 2 * corner, h - 2 * corner, w, h), 0f, 90f, false)
            lineTo(corner, h)
            arcTo(Rect(0f, h - 2 * corner, 2 * corner, h), 90f, 90f, false)
            lineTo(0f, corner)
            arcTo(Rect(0f, 0f, 2 * corner, 2 * corner), 180f, 90f, false)
            close()
        }
        return Outline.Generic(path)
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
fun ModernNavigationBarPreview() {
    ComposeTrainerTheme {
        ModernNavigationBar(
            navController = rememberNavController(),
            currentRoute = Routes.HOME,
            onFabClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ModernNavigationBarPreview_Invoices() {
    ComposeTrainerTheme(darkTheme = true) {
        ModernNavigationBar(
            navController = rememberNavController(),
            currentRoute = Routes.INVOICES_LIST,
            onFabClick = {}
        )
    }
}