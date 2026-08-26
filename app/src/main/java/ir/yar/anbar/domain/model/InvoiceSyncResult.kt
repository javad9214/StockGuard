package ir.yar.anbar.domain.model

/**
 * Outcome of a full invoice sync pass (push + pull).
 */
data class InvoiceSyncResult(
    val pushed: Int = 0,     // invoices uploaded to the server
    val deleted: Int = 0,    // deletions pushed and tombstones removed
    val pulled: Int = 0,     // server invoices merged into the local DB
    val skipped: Int = 0,    // blocked, e.g. line items whose product isn't synced yet
    val failed: Int = 0
) {
    val totalSynced: Int get() = pushed + deleted + pulled
    val hasFailures: Boolean get() = failed > 0
}