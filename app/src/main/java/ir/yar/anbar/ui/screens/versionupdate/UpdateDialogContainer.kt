package ir.yar.anbar.ui.screens.versionupdate


import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import ir.yar.anbar.ui.viewmodels.versionupdate.VersionViewModel

/**
 * Container that manages the update dialog with ViewModel
 * Use this in your app's main screen
 */
@Composable
fun UpdateDialogContainer(
    viewModel: VersionViewModel = hiltViewModel(),
    useCompactDialog: Boolean = false
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Show dialog if needed
    if (uiState.showUpdateDialog) {
        val versionInfo = uiState.versionInfo

        if (versionInfo != null) {
            val currentVersionDisplay = "${uiState.currentVersionName} (${uiState.currentVersionCode})"
            val latestVersionDisplay = "${versionInfo.lastVersionName} (${versionInfo.lastVersionCode})"

            if (useCompactDialog) {
                UpdateDialogCompact(
                    currentVersion = currentVersionDisplay,
                    latestVersion = latestVersionDisplay,
                    updateStatus = uiState.updateStatus,
                    releaseNotes = versionInfo.releaseNotes,
                    onUpdateClick = {
                        viewModel.onUpdateClicked()
                        openUpdateUrl(context, versionInfo.updateUrl)
                    },
                    onSkipClick = {
                        viewModel.onSkipUpdate()
                    },
                    onDismiss = {
                        viewModel.dismissUpdateDialog()
                    }
                )
            } else {
                UpdateDialog(
                    currentVersion = currentVersionDisplay,
                    latestVersion = latestVersionDisplay,
                    updateStatus = uiState.updateStatus,
                    releaseNotes = versionInfo.releaseNotes,
                    onUpdateClick = {
                        viewModel.onUpdateClicked()
                        openUpdateUrl(context, versionInfo.updateUrl)
                    },
                    onSkipClick = {
                        viewModel.onSkipUpdate()
                    },
                    onDismiss = {
                        viewModel.dismissUpdateDialog()
                    }
                )
            }
        }
    }
}

/**
 * Opens the update URL (Play Store or custom URL)
 */
private fun openUpdateUrl(context: Context, updateUrl: String?) {
    try {
        val url = updateUrl ?: getDefaultPlayStoreUrl(context)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
        // Fallback to Play Store if custom URL fails
        try {
            val playStoreUrl = getDefaultPlayStoreUrl(context)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(playStoreUrl))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

/**
 * Gets the default Play Store URL for the app
 */
private fun getDefaultPlayStoreUrl(context: Context): String {
    val packageName = context.packageName
    return "https://play.google.com/store/apps/details?id=$packageName"
}

/**
 * Alternative: Open Play Store app directly
 */
private fun openPlayStoreApp(context: Context) {
    try {
        val packageName = context.packageName
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (e: Exception) {
        // If Play Store app is not available, open in browser
        openUpdateUrl(context, null)
    }
}