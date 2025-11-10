package com.example.composetrainer.ui.components.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.composetrainer.R
import com.example.composetrainer.ui.theme.Beirut_Medium
import com.example.composetrainer.ui.theme.color.customError
import com.example.composetrainer.ui.theme.color.info
import com.example.composetrainer.ui.theme.color.success
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.dimenTextSize
import kotlinx.coroutines.launch

// Confirmation Types
enum class ConfirmyType {
    SUCCESS,
    ERROR,
    INFO
}

@Composable
fun ConfirmyType.getColor(): Color {
    return when (this) {
        ConfirmyType.SUCCESS -> MaterialTheme.colorScheme.success
        ConfirmyType.ERROR -> MaterialTheme.colorScheme.customError
        ConfirmyType.INFO -> MaterialTheme.colorScheme.info
    }
}

// Confirmation State
data class ConfirmyState(
    val message: String = "",
    val type: ConfirmyType = ConfirmyType.INFO,
    val confirmText: String = "Confirm",
    val cancelText: String = "Cancel",
    val isVisible: Boolean = false,
    val onConfirm: () -> Unit = {},
    val onCancel: () -> Unit = {}
)

// Confirmation Host State Manager
class ConfirmyHostState {
    private val _currentConfirmy = mutableStateOf(ConfirmyState())
    val currentConfirmy: State<ConfirmyState> = _currentConfirmy

    fun show(
        message: String,
        type: ConfirmyType,
        confirmText: String = "Confirm",
        cancelText: String = "Cancel",
        onConfirm: () -> Unit = {},
        onCancel: () -> Unit = {}
    ) {
        _currentConfirmy.value = ConfirmyState(
            message = message,
            type = type,
            confirmText = confirmText,
            cancelText = cancelText,
            isVisible = true,
            onConfirm = {
                onConfirm()
                dismiss()
            },
            onCancel = {
                onCancel()
                dismiss()
            }
        )
    }

    fun dismiss() {
        _currentConfirmy.value = _currentConfirmy.value.copy(isVisible = false)
    }
}

@Composable
fun rememberConfirmyHostState(): ConfirmyHostState {
    return remember { ConfirmyHostState() }
}

// Main Confirmation Dialog Component
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Confirmy(
    confirmyState: ConfirmyState,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (confirmyState.isVisible) {
        val typeColor = confirmyState.type.getColor()

        ModalBottomSheet(
            onDismissRequest = { confirmyState.onCancel() },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = null,
            shape = RoundedCornerShape(
                topStart = dimen(R.dimen.radius_lg),
                topEnd = dimen(R.dimen.radius_lg)
            ),
            modifier = modifier
        ) {
            Column {
                // Top Line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimen(R.dimen.size_xxs))
                        .clip(
                            RoundedCornerShape(
                                topStart = dimen(R.dimen.radius_lg),
                                topEnd = dimen(R.dimen.radius_lg)
                            )
                        )
                        .background(typeColor)
                )

                // Message
                Text(
                    text = confirmyState.message,
                    fontFamily = Beirut_Medium,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimen(R.dimen.space_4), horizontal = dimen(R.dimen.space_6))
                )

                // Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // Confirm Button
                    Button(
                        onClick = { confirmyState.onConfirm() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = typeColor
                        ),
                        shape = RoundedCornerShape(dimen(R.dimen.radius_md))
                    ) {
                        Text(
                            confirmyState.confirmText,
                            fontFamily = Beirut_Medium,
                            fontSize = dimenTextSize(R.dimen.text_size_md)
                        )
                    }

                    // Cancel Button
                    OutlinedButton(
                        onClick = { confirmyState.onCancel() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(dimen(R.dimen.radius_md))
                    ) {
                        Text(
                            confirmyState.cancelText,
                            fontFamily = Beirut_Medium,
                            fontSize = dimenTextSize(R.dimen.text_size_md)
                        )
                    }
                }
            }
        }
    }
}

// Confirmation Host
@Composable
fun ConfirmyHost(
    hostState: ConfirmyHostState,
    modifier: Modifier = Modifier
) {
    Confirmy(
        confirmyState = hostState.currentConfirmy.value,
        modifier = modifier
    )
}

// Example Usage
@Composable
fun ConfirmyExample() {
    val confirmyHostState = rememberConfirmyHostState()
    val snackyHostState = rememberSnackyHostState()
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Button(onClick = {
                confirmyHostState.show(
                    message = "Are you sure you want to save this invoice?",
                    type = ConfirmyType.SUCCESS,
                    confirmText = "Save",
                    cancelText = "Cancel",
                    onConfirm = {
                        scope.launch {
                            snackyHostState.show(
                                message = "Invoice saved successfully!",
                                type = SnackyType.SUCCESS
                            )
                        }
                    },
                    onCancel = {
                        scope.launch {
                            snackyHostState.show(
                                message = "Cancelled",
                                type = SnackyType.INFO
                            )
                        }
                    }
                )
            }) {
                Text("Show Success Confirmation")
            }

            Button(onClick = {
                confirmyHostState.show(
                    message = "Are you sure you want to delete this item? This action cannot be undone.",
                    type = ConfirmyType.ERROR,
                    confirmText = "Delete",
                    cancelText = "Keep",
                    onConfirm = {
                        scope.launch {
                            snackyHostState.show(
                                message = "Item deleted",
                                type = SnackyType.SUCCESS
                            )
                        }
                    }
                )
            }) {
                Text("Show Error Confirmation")
            }

            Button(onClick = {
                confirmyHostState.show(
                    message = "Do you want to proceed with this action?",
                    type = ConfirmyType.INFO,
                    confirmText = "Proceed",
                    cancelText = "Cancel",
                    onConfirm = {
                        scope.launch {
                            snackyHostState.show(
                                message = "Action completed",
                                type = SnackyType.INFO
                            )
                        }
                    }
                )
            }) {
                Text("Show Info Confirmation")
            }
        }

        // Global Hosts
        ConfirmyHost(hostState = confirmyHostState)
        SnackyHost(hostState = snackyHostState)
    }
}