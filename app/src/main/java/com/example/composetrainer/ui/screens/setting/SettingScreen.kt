package com.example.composetrainer.ui.screens.setting

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composetrainer.BuildConfig
import com.example.composetrainer.R
import com.example.composetrainer.ui.theme.Beirut_Medium
import com.example.composetrainer.ui.viewmodels.SettingViewModel
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.dimenTextSize
import com.example.composetrainer.utils.str


@Composable
fun SettingScreen(
    onButtonClick: () -> Unit,
    onToggleTheme: () -> Unit = {},
    isDarkTheme: Boolean = false,
    onNavigateBack: () -> Unit,
    settingViewModel: SettingViewModel = hiltViewModel(),
) {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column {
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

                // close icon
                Box(
                    modifier = Modifier
                        .padding(dimen(R.dimen.space_2))
                        .size(dimen(R.dimen.size_lg))
                        .clip(CircleShape)
                        .background(Color.Gray.copy(alpha = 0.08f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = LocalIndication.current,
                            onClick = onNavigateBack
                        )
                        .padding(dimen(R.dimen.space_2))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface ,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }




            }

            Spacer(modifier = Modifier.height(dimen(R.dimen.space_1)))

            CurrencySelector()

            ThemeSelector(
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme
            )
        }

        Text(
            text = "Version: ${BuildConfig.VERSION_NAME}",
            fontFamily = FontFamily.SansSerif,
            fontSize = dimenTextSize(R.dimen.text_size_sm),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = dimen(R.dimen.space_4))
        )

    }


}

const val SETTING_SCREEN_TAG = "SettingScreen"

