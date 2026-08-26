package ir.yar.anbar.data.remote.dto.response

/** Push-sync result: maps each pushed local invoice to its server id. */
data class SyncedInvoiceDto(
    val localId: Long,
    val serverId: Long
)

/**
 * Pull-sync result. serverTime must become the next "since" cursor: the
 * server captures it before running its query, so rows committed during the
 * pull are picked up by the next one.
 */
data class InvoicePullResponseDto(
    val serverTime: Long,
    val content: List<InvoiceResponseDto> = emptyList(),
    val page: Int = 0,
    val size: Int = 0,
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val last: Boolean = true
)

/**
 * One server invoice. Timestamps are epoch millis (the server converts its
 * LocalDateTime columns through its JVM zone on both push and pull, so the
 * values round-trip consistently).
 */
data class InvoiceResponseDto(
    val id: Long,
    val localId: Long? = null,
    val prefix: String? = null,
    val invoiceNumber: Long,
    val invoiceDate: Long? = null,
    val invoiceType: String? = null,
    val customerId: Long? = null,
    val totalAmount: Long? = null,
    val totalProfit: Long? = null,
    val totalDiscount: Long? = null,
    val status: String? = null,
    val paymentMethod: String? = null,
    val notes: String? = null,
    val isDeleted: Boolean? = null,
    val createdAt: Long? = null,
    val updatedAt: Long? = null,
    // Null in list responses; populated in pull-sync and detail reads
    val items: List<InvoiceItemResponseDto>? = null
)

data class InvoiceItemResponseDto(
    val productId: Long,
    val quantity: Int,
    val priceAtSale: Long,
    val costPriceAtTransaction: Long,
    val discount: Long? = null,
    val total: Long? = null
)
