package com.example.composetrainer.ui.screens.setting

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.composetrainer.R
import com.example.composetrainer.ui.theme.Beirut_Medium
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.dimenTextSize
import com.example.composetrainer.utils.str

@Composable
fun ThemeSelector(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {

    ElevatedCard(
        modifier = Modifier.fillMaxWidth().padding(horizontal = dimen(R.dimen.space_2), vertical = dimen(R.dimen.space_4)),
        shape = RoundedCornerShape(dimen(R.dimen.radius_md)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimen(R.dimen.space_6),
                    vertical = dimen(R.dimen.space_4)
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = str(R.string.select_theme),
                    fontFamily = Beirut_Medium,
                    fontSize = dimenTextSize(R.dimen.text_size_lg),
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = if (isDarkTheme) str(R.string.dark) else str(R.string.light),
                    fontSize = dimenTextSize(R.dimen.text_size_sm),
                    color = Color.Gray
                )
            }

            // Modern Toggle Switch
            Box(
                modifier = Modifier
                    .size(width = 64.dp, height = 36.dp)
                    .clip(CircleShape)
                    .background(if (isDarkTheme) Color(0xFF4CAF50) else Color(0xFFE0E0E0))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = LocalIndication.current,
                        onClick = onToggleTheme
                    )
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .align(if (isDarkTheme) Alignment.CenterEnd else Alignment.CenterStart)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isDarkTheme) R.drawable.moon else R.drawable.sun
                        ),
                        contentDescription = if (isDarkTheme) "Dark Mode" else "Light Mode",
                        tint = if (isDarkTheme) Color(0xFF4CAF50) else Color(0xFFFFA000),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(16.dp)

                    )
                }
            }
        }
    }

}