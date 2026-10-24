package ir.yar.anbar.ui.components

import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.Modifier

/**
 * Single source of truth for edge-to-edge system-bar spacing on the bottom
 * edge of a screen.
 *
 * The app draws edge-to-edge (see enableEdgeToEdge in MainActivity), so the
 * system navigation bar — whose height differs between gesture navigation and
 * 3-button navigation — overlaps any content pinned to the bottom. Every
 * bottom-fixed button, bar, list end or snackbar must reserve that space via
 * these modifiers instead of hard-coded dp values.
 */

/**
 * Pads the bottom edge by the system navigation-bar inset so bottom-fixed
 * content (buttons, footers, list ends) is never drawn underneath the
 * gesture pill or the 3-button bar.
 */
fun Modifier.safeBottomPadding(): Modifier = this.navigationBarsPadding()

/**
 * Pads the bottom edge by the navigation-bar inset and the keyboard inset.
 * Use on bottom-fixed buttons of screens with text fields so the button
 * stays visible while typing. navigationBarsPadding() consumes its inset,
 * so when the keyboard is open the two never double-pad.
 */
fun Modifier.safeBottomImePadding(): Modifier = this
    .navigationBarsPadding()
    .imePadding()
