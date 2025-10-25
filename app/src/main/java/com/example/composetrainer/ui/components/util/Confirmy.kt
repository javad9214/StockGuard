package com.example.composetrainer.ui.components.util

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.composetrainer.R
import com.example.composetrainer.ui.theme.Beirut_Medium
import com.example.composetrainer.ui.theme.customError
import com.example.composetrainer.ui.theme.info
import com.example.composetrainer.ui.theme.success
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
@Composable
fun Confirmy(
    confirmyState: ConfirmyState,
    modifier: Modifier = Modifier
) {
    val offsetY by animateDpAsState(
        targetValue = if (confirmyState.isVisible) 0.dp else 400.dp,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "offsetY"
    )

    val alpha by animateFloatAsState(
        targetValue = if (confirmyState.isVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 300,
            easing = LinearEasing
        ),
        label = "alpha"
    )

    if (confirmyState.isVisible || alpha > 0f) {
        val typeColor = confirmyState.type.getColor()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = alpha * 0.5f)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .offset(y = offsetY)
                    .graphicsLayer {
                        this.alpha = alpha
                    },
                shape = RoundedCornerShape(
                    topStart = dimen(R.dimen.radius_md),
                    topEnd = dimen(R.dimen.radius_md)
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = dimen(androidx.cardview.R.dimen.cardview_default_elevation))
            ) {
                Column {
                    // Top Line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(typeColor)
                    )

                    // Message
                    Text(
                        text = confirmyState.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
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