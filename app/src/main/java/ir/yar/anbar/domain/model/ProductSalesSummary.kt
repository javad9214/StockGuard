package ir.yar.anbar.domain.model

import ir.yar.anbar.data.local.entity.ProductSalesSummaryEntity
import ir.yar.anbar.domain.model.type.Money
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.math.BigDecimal
import java.math.RoundingMode

// Domain Model
data class ProductSalesSummary(
    val id: ProductSalesSummaryId,
    val productId: ProductId,
    val date: LocalDate,
    val totalSold: SalesQuantity,
    val totalRevenue: Money,
    val totalCost: Money,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val synced: Boolean
) {
    // Financial calculations
    fun getTotalProfit(): Money {
        return Money(totalRevenue.amount - totalCost.amount)
    }

    fun getProfitMargin(): BigDecimal {
        if (totalRevenue.amount == 0L) return BigDecimal.ZERO

        val profit = getTotalProfit().amount
        return BigDecimal(profit)
            .divide(BigDecimal(totalRevenue.amount), 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal(100))
    }

    fun getAverageSellingPrice(): Money {
        if (totalSold.value == 0) return Money(0L)
        return Money(totalRevenue.amount / totalSold.value)
    }

    fun getAverageCostPrice(): Money {
        if (totalSold.value == 0) return Money(0L)
        return Money(totalCost.amount / totalSold.value)
    }

    fun getUnitProfit(): Money {
        return Money(getAverageSellingPrice().amount - getAverageCostPrice().amount)
    }

    fun getMarkupPercentage(): BigDecimal {
        if (totalCost.amount == 0L) return BigDecimal.ZERO

        val profit = getTotalProfit().amount
        return BigDecimal(profit)
            .divide(BigDecimal(totalCost.amount), 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal(100))
    }

    // Sales performance analysis
    fun isProfitable(): Boolean {
        return getTotalProfit().amount > 0L
    }

    fun isLossDay(): Boolean {
        return getTotalProfit().amount < 0L
    }

    fun isBreakEvenDay(): Boolean {
        return getTotalProfit().amount == 0L
    }

    fun isHighVolumeDay(): Boolean {
        return totalSold.value >= 50 // Configurable threshold
    }

    fun isLowVolumeDay(): Boolean {
        return totalSold.value in 1..5 // Sold something but very little
    }

    fun isZeroSalesDay(): Boolean {
        return totalSold.value == 0
    }

    fun getSalesPerformance(): SalesPerformance {
        return when {
            isZeroSalesDay() -> SalesPerformance.NO_SALES
            isLossDay() -> SalesPerformance.LOSS_MAKING
            isLowVolumeDay() -> SalesPerformance.LOW_VOLUME
            isHighVolumeDay() && isProfitable() -> SalesPerformance.HIGH_VOLUME_PROFITABLE
            isProfitable() -> SalesPerformance.PROFITABLE
            else -> SalesPerformance.BREAK_EVEN
        }
    }

    // Date analysis
    fun isToday(): Boolean {
        return date == LocalDate.now()
    }

    fun isYesterday(): Boolean {
        return date == LocalDate.now().minusDays(1)
    }

    fun isThisWeek(): Boolean {
        val now = LocalDate.now()
        val startOfWeek = now.minusDays(now.dayOfWeek.value.toLong() - 1)
        return date >= startOfWeek
    }

    fun isThisMonth(): Boolean {
        val now = LocalDate.now()
        return date.year == now.year && date.month == now.month
    }

    fun getDaysAgo(): Long {
        return java.time.temporal.ChronoUnit.DAYS.between(date, LocalDate.now())
    }

    fun isRecentSale(): Boolean {
        return getDaysAgo() <= 7
    }

    fun isOldData(): Boolean {
        return getDaysAgo() > 90
    }

    // Business insights
    fun needsSync(): Boolean {
        return !synced && (isProfitable() || isHighVolumeDay())
    }

    fun isSignificantSalesDay(): Boolean {
        return totalRevenue.amount >= 10000L || totalSold.value >= 20 // $100+ or 20+ units
    }

    fun getEfficiencyRatio(): BigDecimal {
        // Revenue per unit sold
        if (totalSold.value == 0) return BigDecimal.ZERO

        return BigDecimal(totalRevenue.amount)
            .divide(BigDecimal(totalSold.value), 2, RoundingMode.HALF_UP)
            .divide(BigDecimal(100), 2, RoundingMode.HALF_UP) // Convert cents to dollars
    }

    fun requiresAttention(): Boolean {
        return isLossDay() || (isHighVolumeDay() && !isProfitable())
    }

    // Formatting helpers
    fun getFormattedDate(): String {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }

    fun getFormattedDateWithDay(): String {
        return date.format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy"))
    }

    fun getSalesSummaryText(): String {
        return "${totalSold.value} units sold for $${totalRevenue.toDisplayAmount()}"
    }

    fun getProfitSummaryText(): String {
        val profit = getTotalProfit()
        val margin = getProfitMargin().setScale(1, RoundingMode.HALF_UP)
        return "Profit: $${profit.toDisplayAmount()} (${margin}%)"
    }
}

// Value Objects
@JvmInline
value class ProductSalesSummaryId(val value: Long)

@JvmInline
value class SalesQuantity(val value: Int) {
    init {
        require(value >= 0) { "Sales quantity cannot be negative" }
        require(value <= 100000) { "Sales quantity cannot exceed 100,000 units per day" }
    }
}

// Enums
enum class SalesPerformance {
    NO_SALES,
    LOSS_MAKING,
    LOW_VOLUME,
    BREAK_EVEN,
    PROFITABLE,
    HIGH_VOLUME_PROFITABLE
}

// Mapping Extension Functions
fun ProductSalesSummaryEntity.toDomain(): ProductSalesSummary {
    return ProductSalesSummary(
        id = ProductSalesSummaryId(id),
        productId = ProductId(productId),
        date = Instant.ofEpochMilli(date)
            .atZone(ZoneId.systemDefault())
            .toLocalDate(),
        totalSold = SalesQuantity(totalSold),
        totalRevenue = Money(totalRevenue),
        totalCost = Money(totalCost),
        createdAt = Instant.ofEpochMilli(createdAt)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime(),
        updatedAt = Instant.ofEpochMilli(updatedAt)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime(),
        synced = synced
    )
}

fun ProductSalesSummary.toEntity(): ProductSalesSummaryEntity {
    return ProductSalesSummaryEntity(
        id = id.value,
        productId = productId.value,
        date = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        totalSold = totalSold.value,
        totalRevenue = totalRevenue.amount,
        totalCost = totalCost.amount,
        createdAt = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        updatedAt = updatedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        synced = synced,
        isDeleted = false
    )
}

// Factory for creating sales summaries
object ProductSalesSummaryFactory {
    fun create(
        productId: Long,
        date: LocalDate,
        totalSold: Int,
        totalRevenue: Long,
        totalCost: Long
    ): ProductSalesSummary {
        val now = LocalDateTime.now()
        return ProductSalesSummary(
            id = ProductSalesSummaryId(0), // Will be set by database
            productId = ProductId(productId),
            date = date,
            totalSold = SalesQuantity(totalSold),
            totalRevenue = Money(totalRevenue),
            totalCost = Money(totalCost),
            createdAt = now,
            updatedAt = now,
            synced = false
        )
    }

    fun createForToday(
        productId: Long,
        totalSold: Int,
        totalRevenue: Long,
        totalCost: Long
    ): ProductSalesSummary {
        return create(
            productId = productId,
            date = LocalDate.now(),
            totalSold = totalSold,
            totalRevenue = totalRevenue,
            totalCost = totalCost
        )
    }

    fun createEmpty(productId: Long, date: LocalDate): ProductSalesSummary {
        return create(
            productId = productId,
            date = date,
            totalSold = 0,
            totalRevenue = 0L,
            totalCost = 0L
        )
    }
}

// Extension functions for updating sales summaries
fun ProductSalesSummary.addSale(
    quantity: Int,
    salePrice: Long,
    costPrice: Long
): ProductSalesSummary {
    val additionalRevenue = salePrice * quantity
    val additionalCost = costPrice * quantity

    return copy(
        totalSold = SalesQuantity(totalSold.value + quantity),
        totalRevenue = Money(totalRevenue.amount + additionalRevenue),
        totalCost = Money(totalCost.amount + additionalCost),
        updatedAt = LocalDateTime.now()
    )
}

fun ProductSalesSummary.adjustSales(
    newTotalSold: Int,
    newTotalRevenue: Long,
    newTotalCost: Long
): ProductSalesSummary {
    return copy(
        totalSold = SalesQuantity(newTotalSold),
        totalRevenue = Money(newTotalRevenue),
        totalCost = Money(newTotalCost),
        updatedAt = LocalDateTime.now()
    )
}

fun ProductSalesSummary.markAsSynced(): ProductSalesSummary {
    return copy(
        synced = true,
        updatedAt = LocalDateTime.now()
    )
}

fun ProductSalesSummary.resetSales(): ProductSalesSummary {
    return copy(
        totalSold = SalesQuantity(0),
        totalRevenue = Money(0L),
        totalCost = Money(0L),
        updatedAt = LocalDateTime.now()
    )
}

// Business analysis extensions
fun ProductSalesSummary.getPerformanceInsight(): String {
    return when (getSalesPerformance()) {
        SalesPerformance.NO_SALES -> "No sales recorded for this day"
        SalesPerformance.LOSS_MAKING -> "Loss-making day - review pricing strategy"
        SalesPerformance.LOW_VOLUME -> "Low volume sales - consider promotion"
        SalesPerformance.BREAK_EVEN -> "Break-even performance"
        SalesPerformance.PROFITABLE -> "Good profitable day"
        SalesPerformance.HIGH_VOLUME_PROFITABLE -> "Excellent high-volume profitable day"
    }
}

fun ProductSalesSummary.getRecommendation(): String {
    return when {
        isLossDay() -> "Review pricing - selling below cost"
        isZeroSalesDay() && !isToday() -> "Consider promotional activities"
        isHighVolumeDay() && getProfitMargin() < BigDecimal(20) -> "High volume but low margin - optimize pricing"
        isProfitable() && getProfitMargin() > BigDecimal(50) -> "Great margins - consider volume growth strategies"
        else -> "Monitor performance and maintain current strategy"
    }
}

// Collection extensions for analytics
fun List<ProductSalesSummary>.getTotalRevenue(): Money {
    return Money(sumOf { it.totalRevenue.amount })
}

fun List<ProductSalesSummary>.getTotalProfit(): Money {
    return Money(sumOf { it.getTotalProfit().amount })
}

fun List<ProductSalesSummary>.getTotalUnitsSold(): Int {
    return sumOf { it.totalSold.value }
}

fun List<ProductSalesSummary>.getAverageProfitMargin(): BigDecimal {
    val profitableDays = filter { it.isProfitable() && it.totalRevenue.amount > 0 }
    if (profitableDays.isEmpty()) return BigDecimal.ZERO

    val totalMargin = profitableDays.fold(BigDecimal.ZERO) { acc, summary ->
        acc.add(summary.getProfitMargin())
    }

    return totalMargin.divide(BigDecimal(profitableDays.size), 2, RoundingMode.HALF_UP)
}

fun List<ProductSalesSummary>.getBestPerformingDay(): ProductSalesSummary? {
    return maxByOrNull { it.getTotalProfit().amount }
}

fun List<ProductSalesSummary>.getWorstPerformingDay(): ProductSalesSummary? {
    return minByOrNull { it.getTotalProfit().amount }
}

fun List<ProductSalesSummary>.getRecentTrend(days: Int = 7): SalesTrend {
    val recentData = filter { it.getDaysAgo() <= days }.sortedBy { it.date }
    if (recentData.size < 2) return SalesTrend.INSUFFICIENT_DATA

    val firstHalf = recentData.take(recentData.size / 2)
    val secondHalf = recentData.drop(recentData.size / 2)

    val firstHalfAvg = firstHalf.map { it.totalRevenue.amount }.average()
    val secondHalfAvg = secondHalf.map { it.totalRevenue.amount }.average()

    return when {
        secondHalfAvg > firstHalfAvg * 1.1 -> SalesTrend.GROWING
        secondHalfAvg < firstHalfAvg * 0.9 -> SalesTrend.DECLINING
        else -> SalesTrend.STABLE
    }
}

fun List<ProductSalesSummary>.getHighPerformanceDays(): List<ProductSalesSummary> {
    return filter { it.getSalesPerformance() == SalesPerformance.HIGH_VOLUME_PROFITABLE }
}

fun List<ProductSalesSummary>.getLowPerformanceDays(): List<ProductSalesSummary> {
    return filter {
        it.getSalesPerformance() in listOf(
            SalesPerformance.NO_SALES,
            SalesPerformance.LOSS_MAKING,
            SalesPerformance.LOW_VOLUME
        )
    }
}

enum class SalesTrend {
    GROWING,
    STABLE,
    DECLINING,
    INSUFFICIENT_DATA
}