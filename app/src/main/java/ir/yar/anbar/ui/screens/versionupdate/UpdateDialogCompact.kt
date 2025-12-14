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
import androidx.compose.ui.unit.sp
import ir.yar.anbar.R
import ir.yar.anbar.data.remote.dto.response.UpdateStatus
import ir.yar.anbar.ui.theme.ComposeTrainerTheme

/**
 * Compact Update Dialog using Material3 AlertDialog
 * Simpler and lighter alternative to the full UpdateDialog
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
                contentDescription = "Update Icon",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = if (isForceUpdate) "Update Required" else "Update Available",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
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
                        "A new version is required to continue using the app."
                    } else {
                        "A new version is available with improvements and bug fixes."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Version comparison
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Current",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = currentVersion,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Text(
                        text = "→",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Latest",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = latestVersion,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Release Notes
                if (!releaseNotes.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "What's New:",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .verticalScroll(rememberScrollState())
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = releaseNotes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onUpdateClick,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (isForceUpdate) "Update Now" else "Update",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        },
        dismissButton = if (!isForceUpdate) {
            {
                TextButton(onClick = onSkipClick) {
                    Text(
                        text = "Skip",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        } else null,
        shape = RoundedCornerShape(20.dp),
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
            releaseNotes = "• Bug fixes\n• Performance improvements\n• New features",
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
            releaseNotes = "• Critical security update\n• Major bug fixes",
            onUpdateClick = {},
            onSkipClick = {},
            onDismiss = {}
        )
    }
}