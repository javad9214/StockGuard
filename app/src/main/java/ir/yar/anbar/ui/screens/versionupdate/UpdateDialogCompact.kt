package ir.yar.anbar.ui.screens.versionupdate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.yar.anbar.R
import ir.yar.anbar.data.remote.dto.response.UpdateStatus
import ir.yar.anbar.ui.theme.BKoodak
import ir.yar.anbar.ui.theme.Beirut_Medium
import ir.yar.anbar.ui.theme.ComposeTrainerTheme
import ir.yar.anbar.utils.dimen
import ir.yar.anbar.utils.dimenTextSize
import ir.yar.anbar.utils.str

/**
 * Compact Update Dialog using Material3 AlertDialog
 * Simpler and lighter alternative to the full UpdateDialog with Persian support
 */
@Composable
fun UpdateDialogCompact(
    currentVersion: String,
    latestVersion: String,
    updateStatus: UpdateStatus,
    releaseNotes: String?,
    onUpdateClick: () -> Unit,
    onSkipClick: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val isForceUpdate = updateStatus == UpdateStatus.UPDATE_REQUIRED
    val canDismiss = !isForceUpdate

    AlertDialog(
        onDismissRequest = {
            if (canDismiss) {
                onDismiss()
            }
        },
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = str(R.string.update_icon),
                modifier = Modifier.size(dimen(R.dimen.size_lg)),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = if (isForceUpdate)
                    str(R.string.update_required)
                else
                    str(R.string.update_available),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = Beirut_Medium
                ),
                fontSize = dimenTextSize(R.dimen.text_size_xxl),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isForceUpdate) {
                        str(R.string.update_required_message)
                    } else {
                        str(R.string.update_available_message)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = Beirut_Medium,
                    fontSize = dimenTextSize(R.dimen.text_size_sm),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(dimen(R.dimen.space_4)))

                // Version comparison
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = str(R.string.current),
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = Beirut_Medium,
                            fontSize = dimenTextSize(R.dimen.text_size_xs),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = currentVersion,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = BKoodak
                            ),
                            fontSize = dimenTextSize(R.dimen.text_size_md),
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Text(
                        text = "←",
                        style = MaterialTheme.typography.headlineMedium,
                        fontFamily = BKoodak,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = str(R.string.latest),
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = Beirut_Medium,
                            fontSize = dimenTextSize(R.dimen.text_size_xs),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = latestVersion,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = BKoodak
                            ),
                            fontSize = dimenTextSize(R.dimen.text_size_md),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Release Notes
                if (!releaseNotes.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(dimen(R.dimen.space_4)))

                    Text(
                        text = str(R.string.whats_new_compact),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = Beirut_Medium
                        ),
                        fontSize = dimenTextSize(R.dimen.text_size_md),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimen(R.dimen.size_5xl))
                            .verticalScroll(rememberScrollState())
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(dimen(R.dimen.radius_sm))
                            )
                            .padding(dimen(R.dimen.space_3))
                    ) {
                        Text(
                            text = releaseNotes,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = Beirut_Medium,
                            fontSize = dimenTextSize(R.dimen.text_size_xs),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = dimenTextSize(R.dimen.text_size_lg)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onUpdateClick,
                shape = RoundedCornerShape(dimen(R.dimen.radius_sm))
            ) {
                Text(
                    text = if (isForceUpdate)
                        str(R.string.update_now)
                    else
                        str(R.string.update),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Beirut_Medium
                    ),
                    fontSize = dimenTextSize(R.dimen.text_size_md)
                )
            }
        },
        dismissButton = if (!isForceUpdate) {
            {
                TextButton(onClick = onSkipClick) {
                    Text(
                        text = str(R.string.skip),
                        style = MaterialTheme.typography.labelLarge,
                        fontFamily = Beirut_Medium,
                        fontSize = dimenTextSize(R.dimen.text_size_md)
                    )
                }
            }
        } else null,
        shape = RoundedCornerShape(dimen(R.dimen.radius_xl)),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    )
}

@Preview(showBackground = true)
@Composable
fun UpdateDialogCompactPreview() {
    ComposeTrainerTheme {
        UpdateDialogCompact(
            currentVersion = "1.0.0",
            latestVersion = "1.2.0",
            updateStatus = UpdateStatus.UPDATE_AVAILABLE,
            releaseNotes = "• رفع اشکالات\n• بهبود عملکرد\n• قابلیت‌های جدید",
            onUpdateClick = {},
            onSkipClick = {},
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UpdateDialogCompactForceUpdatePreview() {
    ComposeTrainerTheme(darkTheme = true) {
        UpdateDialogCompact(
            currentVersion = "1.0.0",
            latestVersion = "2.0.0",
            updateStatus = UpdateStatus.UPDATE_REQUIRED,
            releaseNotes = "• به‌روزرسانی امنیتی مهم\n• رفع اشکالات اساسی",
            onUpdateClick = {},
            onSkipClick = {},
            onDismiss = {}
        )
    }
}