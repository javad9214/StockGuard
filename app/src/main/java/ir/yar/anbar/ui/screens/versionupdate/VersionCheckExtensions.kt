package ir.yar.anbar.ui.screens.versionupdate


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import ir.yar.anbar.ui.viewmodels.versionupdate.VersionViewModel

/**
 * Extension functions and utilities for version checking
 */

/**
 * Automatically check for updates when composable enters composition
 * Use this in your MainActivity or main screen
 */
@Composable
fun AutoCheckForUpdates(
    viewModel: VersionViewModel = hiltViewModel(),
    checkOnlyIfNeeded: Boolean = true
) {
    LaunchedEffect(Unit) {
        if (checkOnlyIfNeeded) {
            if (viewModel.shouldCheckForUpdates()) {
                viewModel.checkForUpdates()
            }
        } else {
            viewModel.checkForUpdates()
        }
    }
}

/**
 * Check for updates silently (no dialog) on app start
 * Useful for background checks
 */
@Composable
fun SilentUpdateCheck(
    viewModel: VersionViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        if (viewModel.shouldCheckForUpdates()) {
            viewModel.silentUpdateCheck()
        }
    }
}

/**
 * Manual update check trigger
 * Use this for "Check for Updates" button in settings
 */
@Composable
fun ManualUpdateCheck(
    viewModel: VersionViewModel = hiltViewModel(),
    trigger: Boolean
) {
    LaunchedEffect(trigger) {
        if (trigger) {
            viewModel.checkForUpdates(showDialogOnUpdate = true)
        }
    }
}