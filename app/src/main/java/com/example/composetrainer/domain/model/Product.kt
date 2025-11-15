package com.example.composetrainer.domain.model

import com.example.composetrainer.data.local.entity.ProductEntity
import com.example.composetrainer.data.remote.dto.ProductDto
import com.example.composetrainer.domain.model.type.Money
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

// Domain Model
data class Product(
    val id: ProductId,
    val name: ProductName,
    val barcode: Barcode?,
    val price: Money, // Selling price
    val costPrice: Money, // Cost price for buying
    val description: ProductDescription?,
    val image: ProductImage?,
    val subcategoryId: SubcategoryId?,
    val supplierId: SupplierId?,
    val unit: ProductUnit?,
    val stock: StockQuantity,
    val minStockLevel: StockQuantity?,
    val maxStockLevel: StockQuantity?,
    val isActive: Boolean,
    val tags: ProductTags?,
    val lastSoldDate: LocalDateTime?,
    val date: LocalDateTime,
    val synced: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    // Stock Management
    fun isInStock(): Boolean {
        return stock.value > 0
    }

    fun isOutOfStock(): Boolean {
        return stock.value == 0
    }

    fun isLowStock(): Boolean {
        return minStockLevel?.let { min ->
            stock.value <= min.value && stock.value > 0
        } ?: false
    }

    fun isOverstocked(): Boolean {
        return maxStockLevel?.let { max ->
            stock.value > max.value
        } ?: false
    }

    fun getStockStatus(): StockStatus {
        return when {
            isOutOfStock() -> StockStatus.OUT_OF_STOCK
            isLowStock() -> StockStatus.LOW_STOCK
            isOverstocked() -> StockStatus.OVERSTOCKED
            else -> StockStatus.NORMAL
        }
    }

    fun getStockPercentage(): BigDecimal {
        val max = maxStockLevel?.value ?: return BigDecimal.ZERO
        if (max == 0) return BigDecimal.ZERO

        return BigDecimal(stock.value)
            .divide(BigDecimal(max), 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal(100))
    }

    fun needsRestock(): Boolean {
        return isOutOfStock() || isLowStock()
    }

    fun getRecommendedOrderQuantity(): StockQuantity? {
        return maxStockLevel?.let { max ->
            val recommended = max.value - stock.value
            if (recommended > 0) StockQuantity(recommended) else null
        }
    }

    // Pricing & Profitability
    fun hasCompletePricing(): Boolean {
        return price != null && costPrice != null
    }

    fun getProfit(): Money? {
        return if (hasCompletePricing()) {
            Money(price!!.amount - costPrice!!.amount)
        } else null
    }

    fun getProfitMargin(): BigDecimal {
        if (!hasCompletePricing() || price!!.amount == 0L) return BigDecimal.ZERO

        val profit = getProfit()!!.amount
        return BigDecimal(profit)
            .divide(BigDecimal(price!!.amount), 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal(100))
    }

    fun getMarkupPercentage(): BigDecimal {
        if (!hasCompletePricing() || costPrice!!.amount == 0L) return BigDecimal.ZERO

        val profit = getProfit()!!.amount
        return BigDecimal(profit)
            .divide(BigDecimal(costPrice!!.amount), 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal(100))
    }

    fun isProfitable(): Boolean {
        return getProfit()?.amount ?: 0L > 0L
    }

    fun isLossProduct(): Boolean {
        return getProfit()?.amount ?: 0L < 0L
    }

    fun getPriceCategory(): PriceCategory {
        val priceAmount = price?.amount ?: return PriceCategory.UNKNOWN
        return when {
            priceAmount == 0L -> PriceCategory.FREE
            priceAmount <= 1000L -> PriceCategory.BUDGET // $10.00
            priceAmount <= 5000L -> PriceCategory.AFFORDABLE // $50.00
            priceAmount <= 20000L -> PriceCategory.PREMIUM // $200.00
            else -> PriceCategory.LUXURY
        }
    }

    // Product Lifecycle & Sales
    fun isRecentlyAdded(): Boolean {
        return createdAt.isAfter(LocalDateTime.now().minusDays(30))
    }

    fun hasBeenSold(): Boolean {
        return lastSoldDate != null
    }

    fun isPopularProduct(): Boolean {
        return lastSoldDate?.isAfter(LocalDateTime.now().minusDays(7)) == true
    }

    fun isSlowMovingProduct(): Boolean {
        return lastSoldDate?.isBefore(LocalDateTime.now().minusMonths(3)) == true
    }

    fun isDeadStock(): Boolean {
        return lastSoldDate?.isBefore(LocalDateTime.now().minusMonths(6)) == true ||
                (lastSoldDate == null && createdAt.isBefore(LocalDateTime.now().minusMonths(3)))
    }

    fun getDaysSinceLastSold(): Long? {
        return lastSoldDate?.let { lastSold ->
            java.time.Duration.between(lastSold, LocalDateTime.now()).toDays()
        }
    }

    fun getProductAge(): Long {
        return java.time.Duration.between(createdAt, LocalDateTime.now()).toDays()
    }

    // Business Rules
    fun canBeSold(): Boolean {
        return isActive && isInStock() && price != null
    }

    fun requiresAttention(): Boolean {
        return needsRestock() || isDeadStock() || !isActive || isLossProduct()
    }

    fun isCompleteProduct(): Boolean {
        return hasCompletePricing() &&
                subcategoryId != null &&
                description != null &&
                minStockLevel != null
    }

    fun needsSync(): Boolean {
        return !synced && isCompleteProduct()
    }

    fun hasImage(): Boolean {
        return image != null
    }

    fun hasTags(): Boolean {
        return tags != null && tags!!.tagList.isNotEmpty()
    }

    fun hasSupplier(): Boolean {
        return supplierId != null
    }

    // Formatting helpers
    fun getFormattedPrice(): String {
        return price?.let { "$${it.toDisplayAmount()}" } ?: "Price not set"
    }

    fun getFormattedCostPrice(): String {
        return costPrice?.let { "$${it.toDisplayAmount()}" } ?: "Cost not set"
    }

    fun getStockDisplayText(): String {
        val unitText = unit?.value ?: "units"
        return "${stock.value} $unitText"
    }
}

// Value Objects
@JvmInline
value class ProductName(val value: String) {
    init {
        require(value.length <= 200) { "Product name cannot exceed 200 characters" }
    }
}

@JvmInline
value class Barcode(val value: String) {
    init {
        require(value.isNotBlank()) { "Barcode cannot be blank" }
        require(value.matches(Regex("^[0-9A-Za-z\\-]+$"))) { "Invalid barcode format" }
        require(value.length <= 50) { "Barcode cannot exceed 50 characters" }
    }
}

@JvmInline
value class ProductDescription(val value: String) {
    init {
        require(value.length <= 5000) { "Product description cannot exceed 1000 characters" }
    }
}

@JvmInline
value class ProductImage(val value: String)

@JvmInline
value class SubcategoryId(val value: Int)

@JvmInline
value class SupplierId(val value: Int)

@JvmInline
value class ProductUnit(val value: String) {
    init {
        require(value.length <= 20) { "Product unit cannot exceed 20 characters" }
    }
}

@JvmInline
value class StockQuantity(val value: Int) {
    init {
        require(value >= 0) { "Stock quantity cannot be negative" }
        require(value <= 1000000) { "Stock quantity cannot exceed 1,000,000" }
    }
}

@JvmInline
value class ProductTags(val value: String) {

    val tagList: List<String>
        get() = value.split(",").map { it.trim() }.filter { it.isNotEmpty() }

    fun hasTag(tag: String): Boolean {
        return tagList.any { it.equals(tag, ignoreCase = true) }
    }
}

// Enums
enum class StockStatus {
    OUT_OF_STOCK,
    LOW_STOCK,
    NORMAL,
    OVERSTOCKED
}

enum class PriceCategory {
    FREE,
    BUDGET,
    AFFORDABLE,
    PREMIUM,
    LUXURY,
    UNKNOWN
}

// Mapping Extension Functions
fun ProductEntity.toDomain(): Product {
    return Product(
        id = ProductId(id),
        name = ProductName(name),
        barcode = barcode?.let { Barcode(it) },
        price = price?.let { Money(it) } ?: Money(0),
        costPrice = costPrice?.let { Money(it) } ?: Money(0),
        description = description?.let { ProductDescription(it) },
        image = image?.let { ProductImage(it) },
        subcategoryId = subcategoryId?.let { SubcategoryId(it) },
        supplierId = supplierId?.let { SupplierId(it) },
        unit = unit?.let { ProductUnit(it) },
        stock = StockQuantity(stock),
        minStockLevel = minStockLevel?.let { StockQuantity(it) },
        maxStockLevel = maxStockLevel?.let { StockQuantity(it) },
        isActive = isActive,
        tags = tags?.let { ProductTags(it) },
        lastSoldDate = lastSoldDate?.let {
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(it),
                ZoneId.systemDefault()
            )
        },
        date = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(date),
            ZoneId.systemDefault()
        ),
        synced = synced,
        createdAt = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(createdAt),
            ZoneId.systemDefault()
        ),
        updatedAt = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(updatedAt),
            ZoneId.systemDefault()
        )
    )
}

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = id.value,
        name = name.value,
        barcode = barcode?.value,
        price = price.amount,
        costPrice = costPrice.amount,
        description = description?.value,
        image = image?.value,
        subcategoryId = subcategoryId?.value,
        supplierId = supplierId?.value,
        unit = unit?.value,
        stock = stock.value,
        minStockLevel = minStockLevel?.value,
        maxStockLevel = maxStockLevel?.value,
        isActive = isActive,
        tags = tags?.value,
        lastSoldDate = lastSoldDate?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
        date = date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        synced = synced,
        createdAt = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        updatedAt = updatedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        isDeleted = false
    )
}

// DTO to Domain Mapping
fun ProductDto.toDomain(): Product {
    return Product(
        id = ProductId(id),
        name = ProductName(name),
        barcode = barcode?.let { Barcode(it) },
        price = price?.let { Money(it) } ?: Money(0),
        costPrice = costPrice?.let { Money(it) } ?: Money(0),
        description = description?.let { ProductDescription(it) },
        image = image?.let { ProductImage(it) },
        subcategoryId = subcategoryId?.let { SubcategoryId(it) },
        supplierId = supplierId?.let { SupplierId(it) },
        unit = unit?.let { ProductUnit(it) },
        stock = StockQuantity(stock),
        minStockLevel = minStockLevel?.let { StockQuantity(it) },
        maxStockLevel = maxStockLevel?.let { StockQuantity(it) },
        isActive = isActive,
        tags = tags?.let { ProductTags(it) },
        lastSoldDate = lastSoldDate?.let {
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(it),
                ZoneId.systemDefault()
            )
        },
        date = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(date),
            ZoneId.systemDefault()
        ),
        synced = true, // Assuming DTOs from API are synced
        createdAt = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(createdAt),
            ZoneId.systemDefault()
        ),
        updatedAt = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(updatedAt),
            ZoneId.systemDefault()
        )
    )
}

// Domain to DTO Mapping
fun Product.toDto(): ProductDto {
    return ProductDto(
        id = id.value,
        name = name.value,
        barcode = barcode?.value,
        price = price.amount,
        costPrice = costPrice.amount,
        description = description?.value,
        image = image?.value,
        subcategoryId = subcategoryId?.value,
        supplierId = supplierId?.value,
        unit = unit?.value,
        stock = stock.value,
        minStockLevel = minStockLevel?.value,
        maxStockLevel = maxStockLevel?.value,
        isActive = isActive,
        tags = tags?.value,
        lastSoldDate = lastSoldDate?.let {
            it.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        },
        date = date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        createdAt = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        updatedAt = updatedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        isDeleted = false // Assuming active products are not deleted
    )
}

// Factory for creating products
object ProductFactory {
    fun createBasic(
        name: ProductName,
        barcode: Barcode?,
        price: Long,
        costPrice: Long,
        initialStock: Int = 0
    ): Product {
        val now = LocalDateTime.now()
        return Product(
            id = ProductId(0),
            name = name,
            barcode = barcode,
            price = Money(price),
            costPrice = Money(costPrice),
            description = null,
            image = null,
            subcategoryId = null,
            supplierId = null,
            unit = null,
            stock = StockQuantity(initialStock),
            minStockLevel = null,
            maxStockLevel = null,
            isActive = true,
            tags = null,
            lastSoldDate = null,
            date = now,
            synced = false,
            createdAt = now,
            updatedAt = now
        )
    }

    fun createComplete(
        id: Long = 0,
        name: String,
        barcode: String,
        price: Long,
        costPrice: Long,
        description: String,
        subcategoryId: Int,
        supplierId: Int,
        unit: String,
        initialStock: Int,
        minStockLevel: Int,
        maxStockLevel: Int,
        tags: String? = null
    ): Product {
        val now = LocalDateTime.now()
        return Product(
            id = ProductId(id),
            name = ProductName(name),
            barcode = Barcode(barcode),
            price = Money(price),
            costPrice = Money(costPrice),
            description = ProductDescription(description),
            image = null,
            subcategoryId = SubcategoryId(subcategoryId),
            supplierId = SupplierId(supplierId),
            unit = ProductUnit(unit),
            stock = StockQuantity(initialStock),
            minStockLevel = StockQuantity(minStockLevel),
            maxStockLevel = StockQuantity(maxStockLevel),
            isActive = true,
            tags = tags?.let { ProductTags(it) },
            lastSoldDate = null,
            date = now,
            synced = false,
            createdAt = now,
            updatedAt = now
        )
    }

}

// Extension functions for updating products
fun Product.updateStock(newStock: Int): Product {
    return copy(
        stock = StockQuantity(newStock),
        updatedAt = LocalDateTime.now()
    )
}

fun Product.addStock(quantity: Int): Product {
    return updateStock(stock.value + quantity)
}

fun Product.reduceStock(quantity: Int): Product {
    val newStock = maxOf(0, stock.value - quantity)
    return updateStock(newStock)
}

fun Product.updatePricing(newPrice: Long, newCostPrice: Long): Product {
    return copy(
        price = Money(newPrice),
        costPrice = Money(newCostPrice),
        updatedAt = LocalDateTime.now()
    )
}

fun Product.recordSale(): Product {
    return copy(
        lastSoldDate = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
}

fun Product.activate(): Product {
    return copy(
        isActive = true,
        updatedAt = LocalDateTime.now()
    )
}

fun Product.deactivate(): Product {
    return copy(
        isActive = false,
        updatedAt = LocalDateTime.now()
    )
}

fun Product.updateStockLevels(minLevel: Int, maxLevel: Int): Product {
    return copy(
        minStockLevel = StockQuantity(minLevel),
        maxStockLevel = StockQuantity(maxLevel),
        updatedAt = LocalDateTime.now()
    )
}

fun Product.addTags(newTags: String): Product {
    val existingTags = tags?.tagList ?: emptyList()
    val tagsToAdd = newTags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    val combinedTags = (existingTags + tagsToAdd).distinct().joinToString(", ")

    return copy(
        tags = ProductTags(combinedTags),
        updatedAt = LocalDateTime.now()
    )
}

fun Product.updateImage(imagePath: String): Product {
    return copy(
        image = ProductImage(imagePath),
        updatedAt = LocalDateTime.now()
    )
}

fun Product.markAsSynced(): Product {
    return copy(
        synced = true,
        updatedAt = LocalDateTime.now()
    )
}

// Business analysis extensions
fun Product.getBusinessInsight(): String {
    return buildString {
        append("Stock: ${getStockStatus()}")
        if (hasCompletePricing()) {
            append(", Margin: ${getProfitMargin().setScale(1, RoundingMode.HALF_UP)}%")
        }
        if (hasBeenSold()) {
            append(", Last sold: ${getDaysSinceLastSold()} days ago")
        }
        if (requiresAttention()) {
            append(" [NEEDS ATTENTION]")
        }
    }
}

// Collection extensions
fun List<Product>.getOutOfStockProducts(): List<Product> {
    return filter { it.isOutOfStock() }
}

fun List<Product>.getLowStockProducts(): List<Product> {
    return filter { it.isLowStock() }
}

fun List<Product>.getDeadStockProducts(): List<Product> {
    return filter { it.isDeadStock() }
}

fun List<Product>.getTotalStockValue(): Money {
    return Money(
        filter { it.hasCompletePricing() }
            .sumOf { it.costPrice.amount * it.stock.value }
    )
}

fun List<Product>.getAverageProfitMargin(): BigDecimal {
    val profitableProducts = filter { it.hasCompletePricing() && it.isProfitable() }
    if (profitableProducts.isEmpty()) return BigDecimal.ZERO

    val totalMargin = profitableProducts.fold(BigDecimal.ZERO) { acc, product ->
        acc.add(product.getProfitMargin())
    }

    return totalMargin.divide(BigDecimal(profitableProducts.size), 2, RoundingMode.HALF_UP)
}