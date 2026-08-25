package ir.yar.anbar.domain.model

/**
 * Outcome of a full push-sync pass over locally pending products.
 */
data class ProductSyncResult(
    val created: Int = 0,
    val updated: Int = 0,
    val deleted: Int = 0,
    val failed: Int = 0
) {
    val totalSynced: Int get() = created + updated + deleted
    val hasFailures: Boolean get() = failed > 0
}
