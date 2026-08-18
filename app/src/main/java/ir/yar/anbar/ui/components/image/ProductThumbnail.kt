package ir.yar.anbar.ui.components.image

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage

// ── Reusable Thumbnail ────────────────────────────────────────────────────────
@Composable
fun ProductThumbnail(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp
) {
    val shape = RoundedCornerShape(8.dp)

    Log.d("ProductThumbnail", "Composing with imageUrl=$imageUrl")

    if (imageUrl != null) {
        SubcomposeAsyncImage(
            model = imageUrl,
            contentDescription = "Product image",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size)
                .clip(shape),
            onLoading = {
                Log.d("ProductThumbnail", "Loading: $imageUrl")
            },
            onSuccess = {
                Log.d("ProductThumbnail", "Success: $imageUrl")
            },
            onError = { state ->
                Log.e("ProductThumbnail", "Error loading $imageUrl", state.result.throwable)
            },
            loading = {
                Box(
                    modifier = Modifier
                        .size(size)
                        .background(MaterialTheme.colorScheme.surfaceVariant, shape),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            error = {
                Box(
                    modifier = Modifier
                        .size(size)
                        .background(MaterialTheme.colorScheme.errorContainer, shape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.BrokenImage,
                        contentDescription = "Image error",
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        )
    } else {
        Log.d("ProductThumbnail", "imageUrl is null, showing placeholder")
        // Placeholder when no image URL
        Box(
            modifier = modifier
                .size(size)
                .background(MaterialTheme.colorScheme.surfaceVariant, shape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = "No image",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}