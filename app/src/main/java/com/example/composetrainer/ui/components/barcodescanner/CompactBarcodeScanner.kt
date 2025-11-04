package com.example.composetrainer.ui.components.barcodescanner

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.composetrainer.R
import com.example.composetrainer.ui.theme.Beirut_Medium
import com.example.composetrainer.utils.dimen
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

private const val TAG = "CompactBarcodeScanner"

@Composable
fun CompactBarcodeScanner(
    onBarcodeDetected: (String) -> Unit,
    modifier: Modifier = Modifier,
    cardRadius: androidx.compose.ui.unit.Dp = 16.dp,
    enablePauseResume: Boolean = true,
    startPaused: Boolean = false
) {
    var isScanning by remember { mutableStateOf(!startPaused) }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(cardRadius)
    ) {
        Box {
            if (isScanning) {
                CompactBarcodeScannerContent(onBarcodeDetected = onBarcodeDetected)
            } else {
                // Paused state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { isScanning = true },
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        painter = painterResource(R.drawable.barcode_scanner_24px),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                        contentDescription = "Barcode Scanner Icon",
                        modifier = Modifier.size(dimen(R.dimen.size_8xl))
                    )

                    Text(
                        text = stringResource(R.string.Tap_to_start_scanning),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = Beirut_Medium,
                    )
                }
            }

            // Pause/Resume button - centered with low opacity
            if (enablePauseResume && isScanning) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp)
                        .clickable { isScanning = false }
                        .background(
                            color = Color.Black.copy(alpha = 0.07f),
                            shape = androidx.compose.foundation.shape.CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.pause),
                        contentDescription = "Pause scanning",
                        tint = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactBarcodeScannerContent(
    onBarcodeDetected: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Create executor and remember it
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    // Clean up executor when composable leaves composition
    DisposableEffect(Unit) {
        onDispose {
            Log.d(TAG, "Shutting down camera executor")
            cameraExecutor.shutdown()
        }
    }

    // Lifecycle observer to pause/resume camera
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    Log.d(TAG, "Lifecycle paused - camera will pause")
                }
                Lifecycle.Event.ON_RESUME -> {
                    Log.d(TAG, "Lifecycle resumed - camera will resume")
                }
                Lifecycle.Event.ON_DESTROY -> {
                    Log.d(TAG, "Lifecycle destroyed")
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Camera permission handling
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Camera permission required", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    try {
                        val cameraProvider = cameraProviderFuture.get()

                        val preview = Preview.Builder()
                            .build()
                            .also { it.setSurfaceProvider(previewView.surfaceProvider) }

                        val imageCapture = ImageCapture.Builder()
                            .build()

                        val imageAnalyzer = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also {
                                it.setAnalyzer(
                                    cameraExecutor,
                                    CompactBarcodeScannerAnalyzer { barcode ->
                                        val vibrator =
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                                val vibratorManager =
                                                    ctx.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                                                vibratorManager.defaultVibrator
                                            } else {
                                                @Suppress("DEPRECATION")
                                                ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                            }

                                        vibrator.vibrate(
                                            VibrationEffect.createOneShot(
                                                100,
                                                VibrationEffect.DEFAULT_AMPLITUDE
                                            )
                                        )

                                        onBarcodeDetected(barcode)
                                    })
                            }

                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        // Unbind all use cases before rebinding
                        cameraProvider.unbindAll()

                        // Bind use cases to lifecycle
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture,
                            imageAnalyzer
                        )

                        Log.d(TAG, "Camera bound successfully")
                    } catch (e: Exception) {
                        Log.e(TAG, "Camera binding failed", e)
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            modifier = Modifier.fillMaxSize(),
            update = { previewView ->
                // Optional: Handle updates if needed
            },
            onRelease = { previewView ->
                // Clean up camera when view is released
                Log.d(TAG, "Releasing camera preview")
                try {
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(previewView.context)
                    val cameraProvider = cameraProviderFuture.get()
                    cameraProvider.unbindAll()
                } catch (e: Exception) {
                    Log.e(TAG, "Error releasing camera", e)
                }
            }
        )

        // Compact overlay
        CompactBarcodeScannerOverlay()
    }
}

@Composable
private fun CompactBarcodeScannerOverlay() {
    // Animated laser line
    val infiniteTransition = rememberInfiniteTransition(label = "laserAnimation")
    val laserY = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserPosition"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        // Draw semi-transparent dark overlay
        drawRect(Color(0x99000000))

        // Scanner takes full width and height with padding
        val padding = 16.dp.toPx()
        val scannerWidth = size.width - (padding * 2)
        val scannerHeight = size.height - (padding * 2)
        val left = padding
        val top = padding

        // Clear scanner rectangle
        drawRect(
            color = Color.Transparent,
            topLeft = Offset(left, top),
            size = Size(scannerWidth, scannerHeight),
            blendMode = BlendMode.Clear
        )

        // Draw scanner rectangle border
        drawRect(
            color = Color.White,
            topLeft = Offset(left, top),
            size = Size(scannerWidth, scannerHeight),
            style = Stroke(width = 3f)
        )

        // Corner length
        val cornerLength = minOf(scannerWidth, scannerHeight) / 6

        // Draw scanner corners (top-left)
        drawLine(
            color = Color(0xFF00FFFF),
            start = Offset(left, top),
            end = Offset(left + cornerLength, top),
            strokeWidth = 4f
        )
        drawLine(
            color = Color(0xFF00FFFF),
            start = Offset(left, top),
            end = Offset(left, top + cornerLength),
            strokeWidth = 4f
        )

        // Top-right
        drawLine(
            color = Color(0xFF00FFFF),
            start = Offset(left + scannerWidth, top),
            end = Offset(left + scannerWidth - cornerLength, top),
            strokeWidth = 4f
        )
        drawLine(
            color = Color(0xFF00FFFF),
            start = Offset(left + scannerWidth, top),
            end = Offset(left + scannerWidth, top + cornerLength),
            strokeWidth = 4f
        )

        // Bottom-left
        drawLine(
            color = Color(0xFF00FFFF),
            start = Offset(left, top + scannerHeight),
            end = Offset(left + cornerLength, top + scannerHeight),
            strokeWidth = 4f
        )
        drawLine(
            color = Color(0xFF00FFFF),
            start = Offset(left, top + scannerHeight),
            end = Offset(left, top + scannerHeight - cornerLength),
            strokeWidth = 4f
        )

        // Bottom-right
        drawLine(
            color = Color(0xFF00FFFF),
            start = Offset(left + scannerWidth, top + scannerHeight),
            end = Offset(left + scannerWidth - cornerLength, top + scannerHeight),
            strokeWidth = 4f
        )
        drawLine(
            color = Color(0xFF00FFFF),
            start = Offset(left + scannerWidth, top + scannerHeight),
            end = Offset(left + scannerWidth, top + scannerHeight - cornerLength),
            strokeWidth = 4f
        )

        // Draw animated laser line
        val laserPositionY = top + scannerHeight * laserY.value
        drawLine(
            color = Color.Red.copy(alpha = 0.7f),
            start = Offset(left, laserPositionY),
            end = Offset(left + scannerWidth, laserPositionY),
            strokeWidth = 2f
        )

        // Add subtle glow effect around the scanner frame
        val glowEffect = 8f
        drawRect(
            color = Color(0x2000FFFF),
            topLeft = Offset(left - glowEffect, top - glowEffect),
            size = Size(scannerWidth + glowEffect * 2, scannerHeight + glowEffect * 2),
            style = Stroke(width = glowEffect)
        )
    }
}

private class CompactBarcodeScannerAnalyzer(
    private val onBarcodeDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient()
    private var lastAnalyzedTimestamp = 0L
    private var lastDetectedBarcode = ""

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        val currentTimestamp = System.currentTimeMillis()

        // Skip analysis if less than 2 seconds have passed since last successful scan
        if (currentTimestamp - lastAnalyzedTimestamp < 2000) {
            imageProxy.close()
            return
        }

        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isNotEmpty()) {
                        barcodes.firstOrNull()?.rawValue?.let { value ->
                            if (value.isNotEmpty() && (value != lastDetectedBarcode || currentTimestamp - lastAnalyzedTimestamp > 5000)) {
                                lastDetectedBarcode = value
                                lastAnalyzedTimestamp = currentTimestamp
                                onBarcodeDetected(value)
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Barcode scanning failed", e)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}