package ir.yar.anbar.data.mapper

import ir.yar.anbar.data.local.entity.InvoiceEntity
import ir.yar.anbar.data.local.entity.InvoiceProductCrossRefEntity
import ir.yar.anbar.data.remote.dto.request.InvoiceItemRequestDto
import ir.yar.anbar.data.remote.dto.request.InvoiceSyncRequestDto
import ir.yar.anbar.data.remote.dto.response.InvoiceItemResponseDto
import ir.yar.anbar.data.remote.dto.response.InvoiceResponseDto

/**
 * Builds the push payload for one local invoice. [serverProductId] resolves a
 * local product row id to its server id; callers only invoke this once every
 * item resolved, so the mapping is total by construction.
 */
fun InvoiceEntity.toSyncRequest(
    items: List<InvoiceProductCrossRefEntity>,
    serverProductId: (Long) -> Long
): InvoiceSyncRequestDto = InvoiceSyncRequestDto(
    localId = id,
    prefix = prefix,
    invoiceNumber = invoiceNumber,
    invoiceDate = invoiceDate,
    invoiceType = invoiceType,
    customerId = customerId,
    totalAmount = totalAmount,
    totalProfit = totalProfit,
    totalDiscount = totalDiscount,
    status = status,
    paymentMethod = paymentMethod,
    notes = notes,
    isDeleted = isDeleted,
    items = items.map { it.toSyncItem(serverProductId) }
)

private fun InvoiceProductCrossRefEntity.toSyncItem(
    serverProductId: (Long) -> Long
): InvoiceItemRequestDto = InvoiceItemRequestDto(
    productId = serverProductId(productId),
    quantity = quantity,
    priceAtSale = priceAtSale,
    costPriceAtTransaction = costPriceAtTransaction,
    discount = discount,
    total = total
)

/** Tombstones push no items — the server clears them when it applies the delete. */
fun InvoiceEntity.toTombstoneRequest(): InvoiceSyncRequestDto = InvoiceSyncRequestDto(
    localId = id,
    prefix = prefix,
    invoiceNumber = invoiceNumber,
    invoiceDate = invoiceDate,
    invoiceType = invoiceType,
    customerId = customerId,
    totalAmount = totalAmount,
    totalProfit = totalProfit,
    totalDiscount = totalDiscount,
    status = status,
    paymentMethod = paymentMethod,
    notes = notes,
    isDeleted = true,
    items = null
)

/**
 * Turns a pulled server invoice into a local row. [localId] reuses the id of
 * an existing row matched by serverId (so REPLACE updates it in place) or is
 * 0 for a fresh insert.
 */
fun InvoiceResponseDto.toEntity(localId: Long): InvoiceEntity = InvoiceEntity(
    id = localId,
    serverId = id,
    prefix = prefix ?: "INV",
    invoiceNumber = invoiceNumber,
    invoiceDate = invoiceDate ?: 0L,
    invoiceType = invoiceType,
    customerId = customerId,
    totalAmount = totalAmount,
    totalProfit = totalProfit,
    totalDiscount = totalDiscount ?: 0L,
    status = status,
    paymentMethod = paymentMethod,
    notes = notes,
    synced = true,
    createdAt = createdAt ?: 0L,
    updatedAt = updatedAt ?: 0L,
    isDeleted = false
)

/**
 * Turns a pulled server line item into a local cross-ref. [localProductId] is
 * the local row id of the product resolved through user_products.serverId.
 */
fun InvoiceItemResponseDto.toCrossRef(
    invoiceId: Long,
    localProductId: Long
): InvoiceProductCrossRefEntity = InvoiceProductCrossRefEntity(
    invoiceId = invoiceId,
    productId = localProductId,
    quantity = quantity,
    priceAtSale = priceAtSale,
    costPriceAtTransaction = costPriceAtTransaction,
    discount = discount ?: 0L,
    total = total ?: (priceAtSale - (discount ?: 0L)) * quantity
)