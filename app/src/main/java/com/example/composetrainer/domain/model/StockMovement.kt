package com.example.composetrainer.domain.model

import com.example.composetrainer.data.local.entity.StockMovementEntity
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.abs

// Domain Model
data class StockMovement(
    val id: StockMovementId,
    val productId: ProductId,
    val quantityChange: QuantityChange,
    val reason: MovementReason,
    val sourceInvoiceId: InvoiceId?,
    val note: MovementNote?,
    val createdAt: LocalDateTime,
    val synced: Boolean
) {
    // Movement type analysis
    fun isInboundMovement(): Boolean {
        return quantityChange.value > 0
    }

    fun isOutboundMovement(): Boolean {
        return quantityChange.value < 0
    }

    fun isZeroMovement(): Boolean {
        return quantityChange.value == 0
    }

    fun getAbsoluteQuantity(): Int {
        return abs(quantityChange.value)
    }

    fun getMovementType(): MovementType {
        return when {
            isInboundMovement() -> MovementType.INBOUND
            isOutboundMovement() -> MovementType.OUTBOUND
            else -> MovementType.ADJUSTMENT
        }
    }

    // Business rules
    fun isSalesRelated(): Boolean {
        return reason == MovementReason.SALE || reason == MovementReason.RETURN
    }

    fun isPurchaseRelated(): Boolean {
        return reason == MovementReason.PURCHASE || reason == MovementReason.PURCHASE_RETURN
    }

    fun isAdjustment(): Boolean {
        return reason in listOf(
            MovementReason.MANUAL_ADJUST,
            MovementReason.DAMAGE,
            MovementReason.EXPIRED,
            MovementReason.LOST,
            MovementReason.THEFT,
            MovementReason.INVENTORY_COUNT
        )
    }

    fun isAutomaticMovement(): Boolean {
        return reason in listOf(
            MovementReason.SALE,
            MovementReason.PURCHASE,
            MovementReason.RETURN,
            MovementReason.PURCHASE_RETURN
        )
    }

    fun isManualMovement(): Boolean {
        return !isAutomaticMovement()
    }

    fun hasInvoiceReference(): Boolean {
        return sourceInvoiceId != null
    }

    fun isSignificantMovement(): Boolean {
        return getAbsoluteQuantity() >= 10 // Configurable threshold
    }

    fun isBulkMovement(): Boolean {
        return getAbsoluteQuantity() >= 50 // Large quantity movement
    }

    // Time analysis
    fun isRecentMovement(): Boolean {
        return createdAt.isAfter(LocalDateTime.now().minusDays(7))
    }

    fun isToday(): Boolean {
        return createdAt.toLocalDate() == LocalDateTime.now().toLocalDate()
    }

    fun isThisWeek(): Boolean {
        val now = LocalDateTime.now()
        val startOfWeek = now.minusDays(now.dayOfWeek.value.toLong() - 1)
        return createdAt.isAfter(startOfWeek)
    }

    fun isThisMonth(): Boolean {
        val now = LocalDateTime.now()
        return createdAt.year == now.year && createdAt.month == now.month
    }

    fun getHoursAgo(): Long {
        return java.time.Duration.between(createdAt, LocalDateTime.now()).toHours()
    }

    fun getDaysAgo(): Long {
        return java.time.Duration.between(createdAt.toLocalDate().atStartOfDay(),
            LocalDateTime.now().toLocalDate().atStartOfDay()).toDays()
    }

    // Business insights
    fun needsSync(): Boolean {
        return !synced && (isSignificantMovement() || isSalesRelated())
    }

    fun requiresReview(): Boolean {
        return (isManualMovement() && isSignificantMovement()) ||
                reason in listOf(MovementReason.DAMAGE, MovementReason.THEFT, MovementReason.LOST)
    }

    fun isNegativeImpact(): Boolean {
        return reason in listOf(
            MovementReason.DAMAGE,
            MovementReason.EXPIRED,
            MovementReason.LOST,
            MovementReason.THEFT
        )
    }

    fun getImpactSeverity(): ImpactSeverity {
        return when {
            isNegativeImpact() && isBulkMovement() -> ImpactSeverity.HIGH
            isNegativeImpact() && isSignificantMovement() -> ImpactSeverity.MEDIUM
            isNegativeImpact() -> ImpactSeverity.LOW
            else -> ImpactSeverity.NONE
        }
    }

    // Formatting helpers
    fun getFormattedDateTime(): String {
        return createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
    }

    fun getFormattedQuantityChange(): String {
        return when {
            isInboundMovement() -> "+${quantityChange.value}"
            isOutboundMovement() -> "${quantityChange.value}" // Already negative
            else -> "${quantityChange.value}"
        }
    }

    fun getMovementDescription(): String {
        val direction = if (isInboundMovement()) "Added" else "Removed"
        val quantity = getAbsoluteQuantity()
        return "$direction $quantity units - ${reason.displayName}"
    }

    fun getDetailedDescription(): String {
        return buildString {
            append(getMovementDescription())
            if (hasInvoiceReference()) {
                append(" (Invoice: ${sourceInvoiceId!!.value})")
            }
            if (note != null) {
                append(" - ${note!!.value}")
            }
        }
    }
}

// Value Objects
@JvmInline
value class StockMovementId(val value: Long) {
    init {
        require(value > 0) { "StockMovement ID must be positive" }
    }
}

@JvmInline
value class QuantityChange(val value: Int) {
    init {
        require(value != 0) { "Quantity change cannot be zero" }
        require(value >= -100000 && value <= 100000) {
            "Quantity change must be between -100,000 and 100,000"
        }
    }
}

@JvmInline
value class MovementNote(val value: String) {
    init {
        require(value.isNotBlank()) { "Movement note cannot be blank" }
        require(value.length <= 500) { "Movement note cannot exceed 500 characters" }
    }
}

// Enums
enum class MovementReason(val code: String, val displayName: String) {
    SALE("SALE", "Sale"),
    PURCHASE("PURCHASE", "Purchase"),
    RETURN("RETURN", "Customer Return"),
    PURCHASE_RETURN("PURCHASE_RETURN", "Purchase Return"),
    MANUAL_ADJUST("MANUAL_ADJUST", "Manual Adjustment"),
    DAMAGE("DAMAGE", "Damaged Goods"),
    EXPIRED("EXPIRED", "Expired Items"),
    LOST("LOST", "Lost/Missing"),
    THEFT("THEFT", "Theft"),
    INVENTORY_COUNT("INVENTORY_COUNT", "Inventory Count Adjustment"),
    TRANSFER_IN("TRANSFER_IN", "Transfer In"),
    TRANSFER_OUT("TRANSFER_OUT", "Transfer Out"),
    RESTOCK("RESTOCK", "Restocking"),
    PROMOTION("PROMOTION", "Promotional Activity");

    companion object {
        fun fromCode(code: String?): MovementReason? {
            return values().find { it.code == code }
        }
    }
}

enum class MovementType {
    INBOUND,
    OUTBOUND,
    ADJUSTMENT
}

enum class ImpactSeverity {
    NONE,
    LOW,
    MEDIUM,
    HIGH
}

// Mapping Extension Functions
fun StockMovementEntity.toDomain(): StockMovement {
    return StockMovement(
        id = StockMovementId(id),
        productId = ProductId(productId),
        quantityChange = QuantityChange(quantityChange),
        reason = MovementReason.fromCode(reason) ?: MovementReason.MANUAL_ADJUST,
        sourceInvoiceId = sourceInvoiceId?.let { InvoiceId(it) },
        note = note?.let { MovementNote(it) },
        createdAt = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(createdAt),
            ZoneId.systemDefault()
        ),
        synced = synced
    )
}

fun StockMovement.toEntity(): StockMovementEntity {
    return StockMovementEntity(
        id = id.value,
        productId = productId.value,
        quantityChange = quantityChange.value,
        reason = reason.code,
        sourceInvoiceId = sourceInvoiceId?.value,
        note = note?.value,
        createdAt = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        synced = synced
    )
}

// Factory for creating stock movements
object StockMovementFactory {
    fun createSale(
        productId: Long,
        quantitySold: Int,
        invoiceId: Long,
        note: String? = null
    ): StockMovement {
        return StockMovement(
            id = StockMovementId(0),
            productId = ProductId(productId),
            quantityChange = QuantityChange(-quantitySold), // Negative for outbound
            reason = MovementReason.SALE,
            sourceInvoiceId = InvoiceId(invoiceId),
            note = note?.let { MovementNote(it) },
            createdAt = LocalDateTime.now(),
            synced = false
        )
    }

    fun createPurchase(
        productId: Long,
        quantityPurchased: Int,
        invoiceId: Long? = null,
        note: String? = null
    ): StockMovement {
        return StockMovement(
            id = StockMovementId(0),
            productId = ProductId(productId),
            quantityChange = QuantityChange(quantityPurchased), // Positive for inbound
            reason = MovementReason.PURCHASE,
            sourceInvoiceId = invoiceId?.let { InvoiceId(it) },
            note = note?.let { MovementNote(it) },
            createdAt = LocalDateTime.now(),
            synced = false
        )
    }

    fun createManualAdjustment(
        productId: Long,
        quantityChange: Int,
        reason: MovementReason = MovementReason.MANUAL_ADJUST,
        note: String
    ): StockMovement {
        return StockMovement(
            id = StockMovementId(0),
            productId = ProductId(productId),
            quantityChange = QuantityChange(quantityChange),
            reason = reason,
            sourceInvoiceId = null,
            note = MovementNote(note),
            createdAt = LocalDateTime.now(),
            synced = false
        )
    }

    fun createDamage(
        productId: Long,
        damagedQuantity: Int,
        note: String
    ): StockMovement {
        return createManualAdjustment(
            productId = productId,
            quantityChange = -damagedQuantity, // Negative for removal
            reason = MovementReason.DAMAGE,
            note = note
        )
    }

    fun createReturn(
        productId: Long,
        returnedQuantity: Int,
        invoiceId: Long,
        note: String? = null
    ): StockMovement {
        return StockMovement(
            id = StockMovementId(0),
            productId = ProductId(productId),
            quantityChange = QuantityChange(returnedQuantity), // Positive for return
            reason = MovementReason.RETURN,
            sourceInvoiceId = InvoiceId(invoiceId),
            note = note?.let { MovementNote(it) },
            createdAt = LocalDateTime.now(),
            synced = false
        )
    }
}

// Extension functions for updating stock movements
fun StockMovement.markAsSynced(): StockMovement {
    return copy(
        synced = true
    )
}

fun StockMovement.addNote(additionalNote: String): StockMovement {
    val existingNote = note?.value ?: ""
    val combinedNote = if (existingNote.isBlank()) {
        additionalNote
    } else {
        "$existingNote | $additionalNote"
    }

    return copy(
        note = MovementNote(combinedNote)
    )
}

// Business analysis extensions
fun StockMovement.getBusinessImpact(): String {
    return when (getImpactSeverity()) {
        ImpactSeverity.HIGH -> "High impact movement - requires immediate attention"
        ImpactSeverity.MEDIUM -> "Medium impact movement - monitor closely"
        ImpactSeverity.LOW -> "Low impact movement - routine tracking"
        ImpactSeverity.NONE -> "Standard business movement"
    }
}

fun StockMovement.getRecommendedAction(): String {
    return when {
        reason == MovementReason.THEFT && isBulkMovement() -> "Investigate security measures"
        reason == MovementReason.DAMAGE && isSignificantMovement() -> "Review handling procedures"
        reason == MovementReason.EXPIRED && isSignificantMovement() -> "Review inventory rotation"
        isManualMovement() && isBulkMovement() -> "Verify accuracy of manual entry"
        else -> "No specific action required"
    }
}

// Collection extensions for analytics
fun List<StockMovement>.getTotalInboundQuantity(): Int {
    return filter { it.isInboundMovement() }.sumOf { it.quantityChange.value }
}

fun List<StockMovement>.getTotalOutboundQuantity(): Int {
    return filter { it.isOutboundMovement() }.sumOf { abs(it.quantityChange.value) }
}

fun List<StockMovement>.getNetMovement(): Int {
    return sumOf { it.quantityChange.value }
}

fun List<StockMovement>.getMovementsByReason(reason: MovementReason): List<StockMovement> {
    return filter { it.reason == reason }
}

fun List<StockMovement>.getSalesMovements(): List<StockMovement> {
    return getMovementsByReason(MovementReason.SALE)
}

fun List<StockMovement>.getPurchaseMovements(): List<StockMovement> {
    return getMovementsByReason(MovementReason.PURCHASE)
}

fun List<StockMovement>.getNegativeImpactMovements(): List<StockMovement> {
    return filter { it.isNegativeImpact() }
}

fun List<StockMovement>.getHighImpactMovements(): List<StockMovement> {
    return filter { it.getImpactSeverity() == ImpactSeverity.HIGH }
}

fun List<StockMovement>.getRecentMovements(days: Int = 7): List<StockMovement> {
    val cutoffDate = LocalDateTime.now().minusDays(days.toLong())
    return filter { it.createdAt.isAfter(cutoffDate) }
}

fun List<StockMovement>.getMovementTrend(): MovementTrend {
    if (size < 2) return MovementTrend.INSUFFICIENT_DATA

    val recent = take(size / 2)
    val older = drop(size / 2)

    val recentInbound = recent.filter { it.isInboundMovement() }.size
    val recentOutbound = recent.filter { it.isOutboundMovement() }.size
    val olderInbound = older.filter { it.isInboundMovement() }.size
    val olderOutbound = older.filter { it.isOutboundMovement() }.size

    val recentRatio = if (recentOutbound == 0) Double.MAX_VALUE else recentInbound.toDouble() / recentOutbound
    val olderRatio = if (olderOutbound == 0) Double.MAX_VALUE else olderInbound.toDouble() / olderOutbound

    return when {
        recentRatio > olderRatio * 1.2 -> MovementTrend.INCREASING_INBOUND
        recentRatio < olderRatio * 0.8 -> MovementTrend.INCREASING_OUTBOUND
        else -> MovementTrend.STABLE
    }
}

fun List<StockMovement>.getMostCommonReason(): MovementReason? {
    return groupBy { it.reason }
        .maxByOrNull { it.value.size }
        ?.key
}

fun List<StockMovement>.getMovementSummary(): String {
    val inbound = getTotalInboundQuantity()
    val outbound = getTotalOutboundQuantity()
    val net = getNetMovement()

    return "In: +$inbound, Out: -$outbound, Net: ${if (net >= 0) "+$net" else "$net"}"
}

enum class MovementTrend {
    INCREASING_INBOUND,
    INCREASING_OUTBOUND,
    STABLE,
    INSUFFICIENT_DATA
}