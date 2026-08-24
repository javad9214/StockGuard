package ir.yar.anbar.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import ir.yar.anbar.R
import ir.yar.anbar.domain.model.User
import ir.yar.anbar.ui.theme.Beirut_Medium
import ir.yar.anbar.ui.theme.color.customError
import ir.yar.anbar.ui.theme.color.success
import ir.yar.anbar.ui.viewmodels.ProfileUiState
import ir.yar.anbar.utils.dateandtime.FarsiDateUtil
import ir.yar.anbar.utils.dimen
import ir.yar.anbar.utils.dimenTextSize
import ir.yar.anbar.utils.str
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 *
 * Profile dropdown anchored to the account icon on the Home header.
 * Shows the authenticated user's full profile information.
 */
@Composable
fun ProfileDropdown(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    state: ProfileUiState,
    onRetry: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier.widthIn(min = 320.dp),
        shape = RoundedCornerShape(dimen(R.dimen.radius_xxl)),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp
    ) {
        when (state) {
            is ProfileUiState.Success -> ProfileContent(user = state.user)
            is ProfileUiState.Error -> ProfileErrorContent(message = state.message, onRetry = onRetry)
            else -> ProfileLoadingContent()
        }

        // Logout stays reachable even when the profile fails to load
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        LogoutItem(onClick = {
            onDismissRequest()
            showLogoutDialog = true
        })
    }

    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirm = {
                showLogoutDialog = false
                onLogout()
            },
            onDismiss = { showLogoutDialog = false }
        )
    }
}

@Composable
private fun LogoutItem(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimen(R.dimen.space_2))
            .clip(RoundedCornerShape(dimen(R.dimen.radius_md)))
            .background(MaterialTheme.colorScheme.customError.copy(alpha = 0.08f))
            .clickable(onClick = onClick)
            .padding(
                horizontal = dimen(R.dimen.space_3),
                vertical = dimen(R.dimen.space_2)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.customError,
            modifier = Modifier.size(dimen(R.dimen.size_xs))
        )
        Spacer(modifier = Modifier.width(dimen(R.dimen.space_2)))
        Text(
            text = str(R.string.logout),
            fontFamily = Beirut_Medium,
            fontSize = dimenTextSize(R.dimen.text_size_sm),
            color = MaterialTheme.colorScheme.customError
        )
    }
}

// Moved from the settings screen — logout now lives in the profile dropdown
@Composable
private fun LogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = str(R.string.logout),
                tint = MaterialTheme.colorScheme.customError,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = str(R.string.logout_confirmation_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = str(R.string.logout_confirmation_message),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.customError
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = str(R.string.logout),
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = str(R.string.cancel),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun ProfileContent(user: User) {
    Column(modifier = Modifier.padding(dimen(R.dimen.space_4))) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            ProfileAvatar(user = user)
            Spacer(modifier = Modifier.width(dimen(R.dimen.space_3)))
            Column {
                Text(
                    text = user.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = Beirut_Medium,
                    fontSize = dimenTextSize(R.dimen.text_size_md),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(dimen(R.dimen.space_1)))
                Text(
                    text = user.phoneNumber,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = Beirut_Medium,
                    fontSize = dimenTextSize(R.dimen.text_size_sm),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(dimen(R.dimen.space_3)))

        Row(horizontalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_2))) {
            InfoPill(
                iconRes = if (user.isAdmin) R.drawable.crown else R.drawable.face_24px,
                text = str(if (user.isAdmin) R.string.profile_role_admin else R.string.profile_role_user),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
            InfoPill(
                iconRes = if (user.enabled) R.drawable.check_24px else R.drawable.block_24px,
                text = str(if (user.enabled) R.string.profile_status_active else R.string.profile_status_disabled),
                containerColor = if (user.enabled) {
                    MaterialTheme.colorScheme.success.copy(alpha = 0.15f)
                } else {
                    MaterialTheme.colorScheme.customError.copy(alpha = 0.15f)
                },
                contentColor = if (user.enabled) {
                    MaterialTheme.colorScheme.success
                } else {
                    MaterialTheme.colorScheme.customError
                }
            )
        }

        Spacer(modifier = Modifier.height(dimen(R.dimen.space_3)))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))

        ProfileInfoRow(
            iconRes = R.drawable.account_circle_24px,
            label = str(R.string.profile_user_id),
            value = user.id.toString()
        )
        ProfileInfoRow(
            iconRes = R.drawable.calendar_today_24px,
            label = str(R.string.profile_member_since),
            value = user.createdAt.toProfileDisplay()
        )
        ProfileInfoRow(
            iconRes = R.drawable.schedule_24px,
            label = str(R.string.profile_last_login),
            value = user.lastLogin.toProfileDisplay(fallback = str(R.string.never))
        )
    }
}

@Composable
private fun ProfileAvatar(user: User) {
    Box(
        modifier = Modifier
            .size(dimen(R.dimen.size_xxl))
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .border(
                width = dimen(R.dimen.stroke_dimen_sm),
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        val imageUrl = user.profileImageUrl
        if (imageUrl == null) {
            UserInitials(user = user)
        } else {
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            strokeWidth = dimen(R.dimen.stroke_dimen_xs),
                            modifier = Modifier.size(dimen(R.dimen.size_sm))
                        )
                    }
                },
                error = {
                    UserInitials(user = user)
                }
            )
        }
    }
}

@Composable
private fun UserInitials(user: User) {
    val initials = user.fullName
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .mapNotNull { it.firstOrNull() }
        .joinToString(" ")

    Text(
        text = initials,
        fontFamily = Beirut_Medium,
        fontSize = dimenTextSize(R.dimen.text_size_md),
        color = MaterialTheme.colorScheme.onPrimaryContainer
    )
}

@Composable
private fun InfoPill(
    iconRes: Int,
    text: String,
    containerColor: Color,
    contentColor: Color
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = containerColor
    ) {
        Row(
            modifier = Modifier.padding(
                start = dimen(R.dimen.space_2),
                end = dimen(R.dimen.space_2),
                top = dimen(R.dimen.space_1),
                bottom = dimen(R.dimen.space_1)
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(dimen(R.dimen.size_xs))
            )
            Spacer(modifier = Modifier.width(dimen(R.dimen.space_1)))
            Text(
                text = text,
                fontFamily = Beirut_Medium,
                fontSize = dimenTextSize(R.dimen.text_size_xs),
                color = contentColor
            )
        }
    }
}

@Composable
private fun ProfileInfoRow(iconRes: Int, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimen(R.dimen.space_2)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(dimen(R.dimen.size_xs))
        )
        Spacer(modifier = Modifier.width(dimen(R.dimen.space_2)))
        Text(
            text = label,
            fontFamily = Beirut_Medium,
            fontSize = dimenTextSize(R.dimen.text_size_sm),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            fontFamily = Beirut_Medium,
            fontSize = dimenTextSize(R.dimen.text_size_sm),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ProfileLoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimen(R.dimen.space_8)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                modifier = Modifier.size(dimen(R.dimen.size_lg)),
                strokeWidth = dimen(R.dimen.stroke_dimen_sm)
            )
            Spacer(modifier = Modifier.height(dimen(R.dimen.space_3)))
            Text(
                text = str(R.string.profile_loading),
                fontFamily = Beirut_Medium,
                fontSize = dimenTextSize(R.dimen.text_size_sm),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ProfileErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimen(R.dimen.space_5)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.error_24px),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.customError,
            modifier = Modifier.size(dimen(R.dimen.size_lg))
        )
        Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))
        Text(
            text = message,
            fontFamily = Beirut_Medium,
            fontSize = dimenTextSize(R.dimen.text_size_sm),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))
        TextButton(onClick = onRetry) {
            Icon(
                painter = painterResource(id = R.drawable.refresh_24px),
                contentDescription = null,
                modifier = Modifier.size(dimen(R.dimen.size_xs))
            )
            Spacer(modifier = Modifier.width(dimen(R.dimen.space_1)))
            Text(
                text = str(R.string.retry),
                fontFamily = Beirut_Medium
            )
        }
    }
}

private fun LocalDateTime?.toProfileDisplay(fallback: String = "—"): String {
    if (this == null) return fallback
    val datePart = FarsiDateUtil.getFormattedPersianDate(this)
    val timePart = format(DateTimeFormatter.ofPattern("HH:mm"))
    return "$datePart • $timePart"
}
