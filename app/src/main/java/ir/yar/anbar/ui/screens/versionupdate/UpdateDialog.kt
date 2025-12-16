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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ir.yar.anbar.R
import ir.yar.anbar.data.remote.dto.response.UpdateStatus
import ir.yar.anbar.ui.theme.BKoodak
import ir.yar.anbar.ui.theme.Beirut_Medium
import ir.yar.anbar.ui.theme.ComposeTrainerTheme
import ir.yar.anbar.utils.dimen
import ir.yar.anbar.utils.dimenTextSize
import ir.yar.anbar.utils.str

/**
 * Update Dialog Component
 * Shows app update information and actions with Persian support
 */
@Composable
fun UpdateDialog(
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

    Dialog(
        onDismissRequest = {
            if (canDismiss) {
                onDismiss()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = canDismiss,
            dismissOnClickOutside = canDismiss,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimen(R.dimen.space_6)),
            shape = RoundedCornerShape(dimen(R.dimen.radius_3xl)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimen(R.dimen.space_6)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon/Image
                Icon(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = str(R.string.update_icon),
                    modifier = Modifier.size(dimen(R.dimen.size_4xl)),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(dimen(R.dimen.space_4)))

                // Title
                Text(
                    text = if (isForceUpdate)
                        str(R.string.update_required)
                    else
                        str(R.string.update_available),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = Beirut_Medium
                    ),
                    fontSize = dimenTextSize(R.dimen.text_size_3xl),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))

                // Subtitle
                Text(
                    text = if (isForceUpdate) {
                        str(R.string.update_required_message)
                    } else {
                        str(R.string.update_available_message)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = Beirut_Medium,
                    fontSize = dimenTextSize(R.dimen.text_size_md),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(dimen(R.dimen.space_5)))

                // Version Info
                VersionInfoRow(
                    label = str(R.string.current_version),
                    version = currentVersion,
                    isOld = true
                )

                Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))

                Icon(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = str(R.string.arrow),
                    modifier = Modifier.size(dimen(R.dimen.size_sm)),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))

                VersionInfoRow(
                    label = str(R.string.latest_version),
                    version = latestVersion,
                    isOld = false
                )

                // Release Notes
                if (!releaseNotes.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(dimen(R.dimen.space_5)))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(dimen(R.dimen.space_4)))

                    Text(
                        text = str(R.string.whats_new),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = Beirut_Medium
                        ),
                        fontSize = dimenTextSize(R.dimen.text_size_xl),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))

                    // Scrollable release notes
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimen(R.dimen.size_6xl))
                            .verticalScroll(rememberScrollState())
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(dimen(R.dimen.radius_md))
                            )
                            .padding(dimen(R.dimen.space_3))
                    ) {
                        Text(
                            text = releaseNotes,
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = Beirut_Medium,
                            fontSize = dimenTextSize(R.dimen.text_size_sm),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = dimenTextSize(R.dimen.text_size_xl)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(dimen(R.dimen.space_6)))

                // Buttons
                if (isForceUpdate) {
                    // Only Update button for force update
                    Button(
                        onClick = onUpdateClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimen(R.dimen.size_xl)),
                        shape = RoundedCornerShape(dimen(R.dimen.radius_md)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = str(R.string.update_now),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = Beirut_Medium
                            ),
                            fontSize = dimenTextSize(R.dimen.text_size_xl)
                        )
                    }
                } else {
                    // Update and Skip buttons for optional update
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_3))
                    ) {
                        OutlinedButton(
                            onClick = onSkipClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(dimen(R.dimen.size_xl)),
                            shape = RoundedCornerShape(dimen(R.dimen.radius_md))
                        ) {
                            Text(
                                text = str(R.string.skip),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = Beirut_Medium
                                ),
                                fontSize = dimenTextSize(R.dimen.text_size_xl)
                            )
                        }

                        Button(
                            onClick = onUpdateClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(dimen(R.dimen.size_xl)),
                            shape = RoundedCornerShape(dimen(R.dimen.radius_md))
                        ) {
                            Text(
                                text = str(R.string.update),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = Beirut_Medium
                                ),
                                fontSize = dimenTextSize(R.dimen.text_size_xl)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Version info row component
 */
@Composable
private fun VersionInfoRow(
    label: String,
    version: String,
    isOld: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isOld)
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                else
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                shape = RoundedCornerShape(dimen(R.dimen.radius_md))
            )
            .padding(dimen(R.dimen.space_4)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = Beirut_Medium,
            fontSize = dimenTextSize(R.dimen.text_size_md),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = version,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = BKoodak
            ),
            fontSize = dimenTextSize(R.dimen.text_size_lg),
            color = if (isOld)
                MaterialTheme.colorScheme.error
            else
                MaterialTheme.colorScheme.primary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UpdateDialogPreview() {
    ComposeTrainerTheme {
        UpdateDialog(
            currentVersion = "1.0.0 (10)",
            latestVersion = "1.2.0 (12)",
            updateStatus = UpdateStatus.UPDATE_AVAILABLE,
            releaseNotes = "• رفع اشکالات\n• بهبود عملکرد\n• افزودن قابلیت‌های جدید\n• بهبود رابط کاربری",
            onUpdateClick = {},
            onSkipClick = {},
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UpdateDialogForceUpdatePreview() {
    ComposeTrainerTheme {
        UpdateDialog(
            currentVersion = "1.0.0 (10)",
            latestVersion = "2.0.0 (20)",
            updateStatus = UpdateStatus.UPDATE_REQUIRED,
            releaseNotes = "• به‌روزرسانی امنیتی مهم\n• رفع اشکالات اساسی\n• تغییرات مهم",
            onUpdateClick = {},
            onSkipClick = {},
            onDismiss = {}
        )
    }
}