package ir.yar.anbar

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import ir.yar.anbar.ui.screens.MainScreen
import ir.yar.anbar.ui.theme.ComposeTrainerTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun attachBaseContext(base: Context) {
        val config = base.resources.configuration
        config.setLocale(Locale("fa"))
        val localizedContext = base.createConfigurationContext(config)
        super.attachBaseContext(localizedContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the activity to portrait mode
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            var isDarkTheme by rememberSaveable { mutableStateOf(false) }
            ComposeTrainerTheme(darkTheme = isDarkTheme) {
                // Update system bars (status bar and navigation bar) colors
                val systemBarsColor =  MaterialTheme.colorScheme.surface
                val systemBarsContrastColor = !isDarkTheme

                SideEffect {
                    val window = this@MainActivity.window
                    window.statusBarColor = systemBarsColor.toArgb()
                    window.navigationBarColor = systemBarsColor.toArgb()

                    // Set the appearance of the status bar and navigation bar icons
                    WindowCompat.getInsetsController(window, window.decorView).apply {
                        isAppearanceLightStatusBars = systemBarsContrastColor
                        isAppearanceLightNavigationBars = systemBarsContrastColor
                    }
                }

                MainScreen(
                    navController = navController,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = { isDarkTheme = !isDarkTheme }
                )
            }
        }
    }
}
