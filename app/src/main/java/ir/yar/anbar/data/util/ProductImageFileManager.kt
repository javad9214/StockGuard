package ir.yar.anbar.data.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Owns product image file handling for sync:
 * - builds the multipart "image" part for uploads from any image reference the
 *   app stores (content:// picker/camera URIs or plain/file:// paths)
 * - persists Base64 image bytes returned by the server into app storage so
 *   images survive across devices and reinstalls
 */
@Singleton
class ProductImageFileManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Builds the multipart upload part for [source]. Returns null when the
     * source is missing or unreadable — content:// URIs are copied to a cache
     * temp file first because OkHttp needs a repeatable request body.
     */
    suspend fun createUploadPart(source: String?): MultipartBody.Part? =
        withContext(Dispatchers.IO) {
            if (source.isNullOrBlank()) return@withContext null

            val file = resolveUploadFile(source) ?: return@withContext null
            val mimeType = resolveMimeType(source) ?: "image/*"
            MultipartBody.Part.createFormData(
                name = "image",
                filename = file.name,
                body = file.asRequestBody(mimeType.toMediaTypeOrNull())
            )
        }

    /**
     * Decodes the Base64 [base64] image the server sent for the product with
     * [serverId] and writes it into filesDir/product_images. Returns a
     * "file://" URI string suitable for Coil, or null when there is nothing
     * to store.
     */
    suspend fun saveServerImage(
        serverId: Long,
        base64: String?,
        imageType: String?
    ): String? = withContext(Dispatchers.IO) {
        if (base64.isNullOrBlank()) return@withContext null

        val bytes = runCatching { Base64.decode(base64, Base64.DEFAULT) }.getOrNull()
        if (bytes == null || bytes.isEmpty()) return@withContext null

        val dir = File(context.filesDir, SERVER_IMAGE_DIR).apply { mkdirs() }
        val target = File(dir, "product_$serverId.${extensionFor(imageType)}")

        runCatching {
            target.writeBytes(bytes)
            Uri.fromFile(target).toString()
        }.getOrNull()
    }

    private suspend fun resolveUploadFile(source: String): File? {
        val uri = runCatching { Uri.parse(source) }.getOrNull()

        // content:// (gallery/camera pickers) — copy to a cache temp file
        if (uri?.scheme == "content") {
            return runCatching {
                val mime = context.contentResolver.getType(uri)
                val name = queryDisplayName(uri)
                    ?: "upload_${System.currentTimeMillis()}.${extensionFor(mime)}"
                val temp = File(context.cacheDir, "upload_$name")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    temp.outputStream().use { output -> input.copyTo(output) }
                } ?: return null
                temp
            }.getOrNull()
        }

        // Plain path or file:// URI
        val path = if (uri?.scheme == "file") uri.path else source
        return path?.let(::File)?.takeIf { it.exists() && it.length() > 0 }
    }

    private fun resolveMimeType(source: String): String? =
        runCatching {
            val uri = Uri.parse(source)
            if (uri.scheme == "content") context.contentResolver.getType(uri)
            else guessMimeFromExtension(source.substringAfterLast('.', ""))
        }.getOrNull()

    private fun queryDisplayName(uri: Uri): String? =
        runCatching {
            context.contentResolver.query(
                uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null
            )?.use { cursor ->
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0 && cursor.moveToFirst()) cursor.getString(index) else null
            }
        }.getOrNull()

    private fun extensionFor(imageType: String?): String = when {
        imageType.isNullOrEmpty() -> "jpg"
        imageType.contains("png", ignoreCase = true) -> "png"
        imageType.contains("webp", ignoreCase = true) -> "webp"
        imageType.contains("gif", ignoreCase = true) -> "gif"
        else -> "jpg"
    }

    private fun guessMimeFromExtension(extension: String): String? = when (extension.lowercase()) {
        "png" -> "image/png"
        "webp" -> "image/webp"
        "gif" -> "image/gif"
        "jpg", "jpeg" -> "image/jpeg"
        else -> null
    }

    private companion object {
        const val SERVER_IMAGE_DIR = "product_images"
    }
}
