package ir.yar.anbar.ui.components.barcodescanner

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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import ir.yar.anbar.R
import ir.yar.anbar.ui.theme.BNazanin
import ir.yar.anbar.utils.str
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

private const val TAG = "BarcodeScannerView"

@Composable
fun BarcodeScannerView(
    onBarcodeDetected: (String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    // Camera permission handling
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Camera permission required", Toast.LENGTH_LONG).show()
            onClose()
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
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
                                        BarcodeScannerAnalyzer { barcode ->
                                            val vibrator =
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                                    val vibratorManager =
                                                        ctx.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                                                    vibratorManager.defaultVibrator
                                                } else {
                                                    @Suppress("DEPRECATION")
                                                    ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                                }

                                            // Vibrate with compatibility for different API levels
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                vibrator.vibrate(
                                                    VibrationEffect.createOneShot(
                                                        100,
                                                        VibrationEffect.DEFAULT_AMPLITUDE
                                                    )
                                                )
                                            } else {
                                                @Suppress("DEPRECATION")
                                                vibrator.vibrate(100)
                                            }

                                            onBarcodeDetected(barcode)
                                        })
                                }

                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageCapture,
                                imageAnalyzer
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, "Camera binding failed", e)
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )

            // Barcode scanner overlay
            BarcodeScannerOverlay()

            // Close button
            FloatingActionButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }
    }
}

@Composable
private fun BarcodeScannerOverlay() {
    val scannerHeight = 250.dp
    val scannerWidth = 250.dp
    val density = LocalDensity.current

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

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Semi-transparent dark background with clear scanner rectangle
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw semi-transparent dark overlay
            drawRect(Color(0x99000000))

            // Calculate center and scanner rectangle dimensions
            val centerX = size.width / 2
            val centerY = size.height / 2
            val scannerWidthPx = with(density) { scannerWidth.toPx() }
            val scannerHeightPx = with(density) { scannerHeight.toPx() }

            // Clear scanner rectangle
            drawRect(
                color = Color.Transparent,
                topLeft = Offset(centerX - scannerWidthPx / 2, centerY - scannerHeightPx / 2),
                size = Size(scannerWidthPx, scannerHeightPx),
                blendMode = BlendMode.Clear
            )

            // Draw scanner rectangle border
            drawRect(
                color = Color.White,
                topLeft = Offset(centerX - scannerWidthPx / 2, centerY - scannerHeightPx / 2),
                size = Size(scannerWidthPx, scannerHeightPx),
                style = Stroke(width = 4f)
            )

            // Draw scanner corners (top-left)
            drawLine(
                color = Color(0xFF00FFFF),
                start = Offset(centerX - scannerWidthPx / 2, centerY - scannerHeightPx / 2),
                end = Offset(centerX - scannerWidthPx / 2 + scannerWidthPx / 8, centerY - scannerHeightPx / 2),
                strokeWidth = 4f
            )
            drawLine(
                color = Color(0xFF00FFFF),
                start = Offset(centerX - scannerWidthPx / 2, centerY - scannerHeightPx / 2),
                end = Offset(centerX - scannerWidthPx / 2, centerY - scannerHeightPx / 2 + scannerHeightPx / 8),
                strokeWidth = 4f
            )

            // Top-right
            drawLine(
                color = Color(0xFF00FFFF),
                start = Offset(centerX + scannerWidthPx / 2, centerY - scannerHeightPx / 2),
                end = Offset(centerX + scannerWidthPx / 2 - scannerWidthPx / 8, centerY - scannerHeightPx / 2),
                strokeWidth = 4f
            )
            drawLine(
                color = Color(0xFF00FFFF),
                start = Offset(centerX + scannerWidthPx / 2, centerY - scannerHeightPx / 2),
                end = Offset(centerX + scannerWidthPx / 2, centerY - scannerHeightPx / 2 + scannerHeightPx / 8),
                strokeWidth = 4f
            )

            // Bottom-left
            drawLine(
                color = Color(0xFF00FFFF),
                start = Offset(centerX - scannerWidthPx / 2, centerY + scannerHeightPx / 2),
                end = Offset(centerX - scannerWidthPx / 2 + scannerWidthPx / 8, centerY + scannerHeightPx / 2),
                strokeWidth = 4f
            )
            drawLine(
                color = Color(0xFF00FFFF),
                start = Offset(centerX - scannerWidthPx / 2, centerY + scannerHeightPx / 2),
                end = Offset(centerX - scannerWidthPx / 2, centerY + scannerHeightPx / 2 - scannerHeightPx / 8),
                strokeWidth = 4f
            )

            // Bottom-right
            drawLine(
                color = Color(0xFF00FFFF),
                start = Offset(centerX + scannerWidthPx / 2, centerY + scannerHeightPx / 2),
                end = Offset(centerX + scannerWidthPx / 2 - scannerWidthPx / 8, centerY + scannerHeightPx / 2),
                strokeWidth = 4f
            )
            drawLine(
                color = Color(0xFF00FFFF),
                start = Offset(centerX + scannerWidthPx / 2, centerY + scannerHeightPx / 2),
                end = Offset(centerX + scannerWidthPx / 2, centerY + scannerHeightPx / 2 - scannerHeightPx / 8),
                strokeWidth = 4f
            )

            // Draw animated laser line
            val laserPositionY = centerY - scannerHeightPx / 2 + scannerHeightPx * laserY.value
            drawLine(
                color = Color.Red.copy(alpha = 0.7f),
                start = Offset(centerX - scannerWidthPx / 2, laserPositionY),
                end = Offset(centerX + scannerWidthPx / 2, laserPositionY),
                strokeWidth = 2f
            )

            // Add subtle glow effect around the scanner frame
            val glowEffect = 12f
            drawRect(
                color = Color(0x2000FFFF),
                topLeft = Offset(
                    centerX - scannerWidthPx / 2 - glowEffect,
                    centerY - scannerHeightPx / 2 - glowEffect
                ),
                size = Size(
                    scannerWidthPx + glowEffect * 2,
                    scannerHeightPx + glowEffect * 2
                ),
                style = Stroke(width = glowEffect)
            )
        }

        // Instruction text
        Text(
            text = str(R.string.barcode_position),
            color = Color.White,
            fontSize = 16.sp,
            fontFamily = BNazanin,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        )
    }
}

private class BarcodeScannerAnalyzer(
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
                        // Only process the first barcode
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
