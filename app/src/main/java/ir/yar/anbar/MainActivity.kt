package ir.yar.anbar

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import ir.yar.anbar.ui.screens.MainScreen
import ir.yar.anbar.ui.theme.ComposeTrainerTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun attachBaseContext(base: Context) {
        val config = base.resources.configuration
        config.setLocale(Locale("fa"))
        config.fontScale = 1f
        super.attachBaseContext(base.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Initial call before setContent - avoids a flash of wrong icon color
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
        )

        setContent {
            val navController = rememberNavController()
            var isDarkTheme by rememberSaveable { mutableStateOf(false) }

            // Re-apply whenever theme toggles, since icon appearance
            // must go through enableEdgeToEdge, not window properties
            LaunchedEffect(isDarkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = if (isDarkTheme)
                        SystemBarStyle.dark(Color.TRANSPARENT)
                    else
                        SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
                    navigationBarStyle = if (isDarkTheme)
                        SystemBarStyle.dark(Color.TRANSPARENT)
                    else
                        SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                )
            }

            ComposeTrainerTheme(darkTheme = isDarkTheme) {
                MainScreen(
                    navController = navController,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = { isDarkTheme = !isDarkTheme }
                )
            }
        }
    }
}