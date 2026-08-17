package ir.yar.anbar.domain.model

data class ProductImage(
    val localUri: String? = null,   // content:// or file:// path (Room, pending upload)
    val remoteUrl: String? = null   // server URL after successful sync
) {
    val isSynced: Boolean get() = remoteUrl != null
    val displayPath: String? get() = remoteUrl ?: localUri
}