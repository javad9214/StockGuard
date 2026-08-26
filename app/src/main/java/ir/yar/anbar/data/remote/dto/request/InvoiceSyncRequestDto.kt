package ir.yar.anbar.data.remote.dto.request

/**
 * Push-sync payload: an invoice exactly as it exists on this device.
 * localId is the Room row id; the server upserts by (userId, localId) so a
 * retried batch can never duplicate an invoice.
 */
data class InvoiceSyncRequestDto(
    val localId: Long,
    val prefix: String? = null,
    val invoiceNumber: Long,
    val invoiceDate: Long,
    val invoiceType: String? = null,
    val customerId: Long? = null,
    val totalAmount: Long? = null,
    val totalProfit: Long? = null,
    val totalDiscount: Long? = null,
    val status: String? = null,
    val paymentMethod: String? = null,
    val notes: String? = null,
    val isDeleted: Boolean? = null,
    // Null for tombstones — the server clears items when it receives a deletion
    val items: List<InvoiceItemRequestDto>? = null
)

data class InvoiceItemRequestDto(
    // Server-side product id — the device's local products already carry serverId
    val productId: Long,
    val quantity: Int,
    val priceAtSale: Long,
    val costPriceAtTransaction: Long,
    val discount: Long,
    val total: Long
)
