package ir.yar.anbar.ui.screens.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import ir.yar.anbar.BuildConfig
import ir.yar.anbar.R
import ir.yar.anbar.ui.components.util.SnackyDuration
import ir.yar.anbar.ui.components.util.SnackyHost
import ir.yar.anbar.ui.components.util.SnackyType
import ir.yar.anbar.ui.components.util.rememberSnackyHostState
import ir.yar.anbar.ui.theme.Beirut_Medium
import ir.yar.anbar.ui.viewmodels.SettingViewModel
import ir.yar.anbar.utils.dimen
import ir.yar.anbar.utils.dimenTextSize
import ir.yar.anbar.utils.str


@Composable
fun SettingScreen(
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {},
    onNavigateBack: () -> Unit,
    settingViewModel: SettingViewModel = hiltViewModel(),
) {

    val uiState by settingViewModel.uiState.collectAsState()
    val snackyHostState = rememberSnackyHostState()

    // Surface preference load/save failures as a snackbar, then drop the
    // message so it isn't re-displayed on recomposition
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackyHostState.show(
                message = message,
                type = SnackyType.ERROR,
                duration = SnackyDuration.LONG
            )
            settingViewModel.onErrorMessageShown()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(start = dimen(R.dimen.space_6), end = dimen(R.dimen.space_2)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    str(R.string.setting),
                    fontFamily = Beirut_Medium,
                    fontSize = dimenTextSize(R.dimen.text_size_xl)
                )

                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = str(R.string.close)
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimen(R.dimen.space_1)))

            CurrencySelector()

            ThemeSelector(
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme
            )

            StockRunoutLimitSelector(
                limit = uiState.stockRunoutLimit,
                onLimitChange = { settingViewModel.saveStockRunoutLimit(it) }
            )

            SettingsVersionSection()

            // Room for the pinned version label so it never overlaps the last section
            Spacer(modifier = Modifier.height(dimen(R.dimen.space_8)))

        }

        Text(
            text = str(R.string.app_version, BuildConfig.VERSION_NAME),
            fontFamily = Beirut_Medium,
            fontSize = dimenTextSize(R.dimen.text_size_sm),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = dimen(R.dimen.space_4))
        )

        SnackyHost(hostState = snackyHostState)

    }


}
