package com.example.composetrainer.ui.screens.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composetrainer.R
import com.example.composetrainer.ui.components.SnackyHost
import com.example.composetrainer.ui.components.SnackyType
import com.example.composetrainer.ui.components.rememberSnackyHostState
import com.example.composetrainer.ui.theme.Beirut_Medium
import com.example.composetrainer.ui.viewmodels.SettingViewModel
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.dimenTextSize
import com.example.composetrainer.utils.str
import kotlinx.coroutines.launch


@Composable
fun SettingScreen(
    onButtonClick: () -> Unit,
    onToggleTheme: () -> Unit = {},
    isDarkTheme: Boolean = false,
    onNavigateBack: () -> Unit,
    settingViewModel: SettingViewModel = hiltViewModel(),
) {

    val snackyHostState = rememberSnackyHostState()
    val scope = rememberCoroutineScope()
    val message = str(R.string.no_product_found_with_barcode)
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
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
                        painter = painterResource(id = R.drawable.arrow_back_ios_new_24px),
                        contentDescription = str(R.string.back)
                    )
                }


            }

            Spacer(modifier = Modifier.padding(vertical = dimen(R.dimen.space_4)))

            CurrencySelector()
        }

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(onClick = {
                    scope.launch {
                        snackyHostState.show(
                            message = "Operation completed successfully!",
                            type = SnackyType.SUCCESS
                        )
                    }
                }) {
                    Text("Show Success")
                }

                Button(onClick = {

                    scope.launch {
                        snackyHostState.show(
                            message = message,
                            type = SnackyType.ERROR
                        )
                    }
                }) {
                    Text("Show Error")
                }

                Button(onClick = {
                    scope.launch {
                        snackyHostState.show(
                            message = "Here's some information for you",
                            type = SnackyType.INFO,
                            duration = 5000L
                        )
                    }
                }) {
                    Text("Show Info (5s)")
                }
            }

            // Snackbar Host at bottom
            SnackyHost(hostState = snackyHostState)
        }

    }


}

const val SETTING_SCREEN_TAG = "SettingScreen"

