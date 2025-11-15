package com.example.composetrainer.ui.components.util


import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.composetrainer.R
import com.example.composetrainer.ui.theme.BRoya
import com.example.composetrainer.ui.theme.color.customError
import com.example.composetrainer.ui.theme.color.info
import com.example.composetrainer.ui.theme.color.success
import com.example.composetrainer.utils.dimenTextSize
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Snackbar Duration
enum class SnackyDuration(val milliseconds: Long) {
    SHORT(2000L),
    LONG(5000L),
    INFINITE(Long.MAX_VALUE)
}

// Snackbar Types
enum class SnackyType(val icon: ImageVector?) {
    SUCCESS(Icons.Default.CheckCircle),
    ERROR(Icons.Default.Error),
    INFO(Icons.Default.Info),
    LOADING(null) // No icon, will show CircularProgressIndicator
}

@Composable
fun SnackyType.getColor(): Color {
    return when (this) {
        SnackyType.SUCCESS -> MaterialTheme.colorScheme.success
        SnackyType.ERROR -> MaterialTheme.colorScheme.customError
        SnackyType.INFO -> MaterialTheme.colorScheme.info
        SnackyType.LOADING -> MaterialTheme.colorScheme.primary
    }
}

// Snackbar State
data class SnackyState(
    val message: String = "",
    val type: SnackyType = SnackyType.INFO,
    val duration: Long = 3000L,
    val isVisible: Boolean = false
)

// Snackbar Host State Manager
class SnackyHostState {
    private val _currentSnacky = mutableStateOf(SnackyState())
    val currentSnacky: State<SnackyState> = _currentSnacky

    suspend fun show(
        message: String,
        type: SnackyType,
        duration: SnackyDuration = SnackyDuration.SHORT
    ) {
        _currentSnacky.value = SnackyState(
            message = message,
            type = type,
            duration = duration.milliseconds,
            isVisible = true
        )

        // For LOADING type or INFINITE duration, don't auto-dismiss
        if (type != SnackyType.LOADING && duration != SnackyDuration.INFINITE) {
            delay(duration.milliseconds + 300) // Extra time for animation to complete
            _currentSnacky.value = _currentSnacky.value.copy(isVisible = false)
        }
    }

    fun dismiss() {
        _currentSnacky.value = _currentSnacky.value.copy(isVisible = false)
    }
}

@Composable
fun rememberSnackyHostState(): SnackyHostState {
    return remember { SnackyHostState() }
}

// Main Snackbar Component
@Composable
fun Snacky(
    snackyState: SnackyState,
    modifier: Modifier = Modifier
) {
    val offsetY by animateDpAsState(
        targetValue = if (snackyState.isVisible) 0.dp else 100.dp,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "offsetY"
    )

    val alpha by animateFloatAsState(
        targetValue = if (snackyState.isVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 300,
            easing = LinearEasing
        ),
        label = "alpha"
    )

    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(snackyState.isVisible, snackyState.type) {
        if (snackyState.isVisible && snackyState.type != SnackyType.LOADING) {
            progress = 0f
            val startTime = System.currentTimeMillis()
            while (progress < 1f) {
                val elapsed = System.currentTimeMillis() - startTime
                progress = (elapsed.toFloat() / snackyState.duration).coerceAtMost(1f)
                delay(16) // ~60fps
            }
        } else {
            progress = 0f
        }
    }

    if (snackyState.isVisible || alpha > 0f) {
        val typeColor = snackyState.type.getColor()

        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .offset(y = offsetY)
                .graphicsLayer {
                    this.alpha = alpha
                },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column {
                // Progress Line (only show for non-LOADING types)
                if (snackyState.type != SnackyType.LOADING) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .fillMaxHeight()
                                .background(typeColor)
                        )
                    }
                } else {
                    // For LOADING, show animated progress bar
                    val infiniteTransition = rememberInfiniteTransition(label = "loading")
                    val animatedProgress by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, easing = LinearEasing)
                        ),
                        label = "loading_progress"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedProgress)
                                .fillMaxHeight()
                                .background(typeColor)
                        )
                    }
                }

                // Content
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Show CircularProgressIndicator for LOADING, Icon for others
                    if (snackyState.type == SnackyType.LOADING) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = typeColor,
                            strokeWidth = 2.dp
                        )
                    } else {
                        snackyState.type.icon?.let { icon ->
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = typeColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Text(
                        text = snackyState.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                        fontFamily = BRoya,
                        fontSize = dimenTextSize(R.dimen.text_size_lg)
                    )
                }
            }
        }
    }
}

// Snackbar Host - Can be placed anywhere!
@Composable
fun SnackyHost(
    hostState: SnackyHostState,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.BottomCenter
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = alignment
    ) {
        Snacky(
            snackyState = hostState.currentSnacky.value,
            modifier = Modifier.padding(16.dp)
        )
    }
}

// Example 1: Simple Usage (No Scaffold needed!)
@Composable
fun SnackySimpleExample() {
    val snackyHostState = rememberSnackyHostState()
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        // Your content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                scope.launch {
                    snackyHostState.show(
                        message = "Success!",
                        type = SnackyType.SUCCESS
                    )
                }
            }) {
                Text("Show Snacky")
            }
        }

        // Just add SnackyHost anywhere!
        SnackyHost(hostState = snackyHostState)
    }
}

// Example 2: With Scaffold
@Composable
fun SnackyWithScaffoldExample() {
    val snackyHostState = rememberSnackyHostState()
    val scope = rememberCoroutineScope()

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
            ) {
                Button(onClick = {
                    scope.launch {
                        snackyHostState.show(
                            message = "Operation completed successfully!",
                            type = SnackyType.SUCCESS,
                            duration = SnackyDuration.SHORT
                        )
                    }
                }) {
                    Text("Show Success")
                }

                Button(onClick = {
                    scope.launch {
                        snackyHostState.show(
                            message = "Something went wrong!",
                            type = SnackyType.ERROR,
                            duration = SnackyDuration.LONG
                        )
                    }
                }) {
                    Text("Show Error (Long)")
                }

                Button(onClick = {
                    scope.launch {
                        snackyHostState.show(
                            message = "Here's some information for you",
                            type = SnackyType.INFO,
                            duration = SnackyDuration.LONG
                        )
                    }
                }) {
                    Text("Show Info (5s)")
                }
            }

            // SnackyHost can be placed here
            SnackyHost(hostState = snackyHostState)
        }
    }
}

// Example 3: Custom Position (Top alignment)
@Composable
fun SnackyTopExample() {
    val snackyHostState = rememberSnackyHostState()
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = {
                scope.launch {
                    snackyHostState.show(
                        message = "Notification from top!",
                        type = SnackyType.INFO
                    )
                }
            },
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text("Show Top Snacky")
        }

        // Show at top instead of bottom
        SnackyHost(
            hostState = snackyHostState,
            alignment = Alignment.TopCenter
        )
    }
}

// Example 4: Loading Type - Manual Dismiss
@Composable
fun SnackyLoadingExample() {
    val snackyHostState = rememberSnackyHostState()
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Button(onClick = {
                scope.launch {
                    // Show loading
                    snackyHostState.show(
                        message = "Loading data...",
                        type = SnackyType.LOADING
                    )

                    // Simulate some work
                    delay(3000)

                    // Dismiss loading and show success
                    snackyHostState.dismiss()
                    delay(100) // Small delay for smooth transition
                    snackyHostState.show(
                        message = "Data loaded successfully!",
                        type = SnackyType.SUCCESS,
                        duration = SnackyDuration.SHORT
                    )
                }
            }) {
                Text("Show Loading")
            }

            Button(onClick = {
                scope.launch {
                    // Show loading
                    snackyHostState.show(
                        message = "Processing...",
                        type = SnackyType.LOADING
                    )

                    // Simulate work that fails
                    delay(2000)

                    // Dismiss loading and show error
                    snackyHostState.dismiss()
                    delay(100)
                    snackyHostState.show(
                        message = "Failed to process!",
                        type = SnackyType.ERROR,
                        duration = SnackyDuration.LONG
                    )
                }
            }) {
                Text("Show Loading with Error")
            }

            Button(onClick = {
                scope.launch {
                    // Show infinite duration - stays until manually dismissed
                    snackyHostState.show(
                        message = "This stays forever until dismissed!",
                        type = SnackyType.INFO,
                        duration = SnackyDuration.INFINITE
                    )
                }
            }) {
                Text("Show Infinite")
            }

            Button(onClick = {
                scope.launch {
                    snackyHostState.dismiss()
                }
            }) {
                Text("Dismiss Current Snacky")
            }
        }

        SnackyHost(hostState = snackyHostState)
    }
}