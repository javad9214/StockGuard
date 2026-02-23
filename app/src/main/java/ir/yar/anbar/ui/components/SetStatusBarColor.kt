package ir.yar.anbar.ui.components

import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat


@Composable
fun SetStatusBarColor(color: Color, darkIcons: Boolean = true) {
    val activity = LocalActivity.current
    activity?.let {
        val window = it.window
        val colorInt = color.toArgb()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 14+ (API 34+): Use the new API
            window.statusBarColor = colorInt
            window.navigationBarColor = colorInt
        } else {
            @Suppress("DEPRECATION")
            window.statusBarColor = colorInt
            window.navigationBarColor = colorInt
        }

        // Set icon color (light/dark)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = darkIcons
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightNavigationBars = true
    }
}



