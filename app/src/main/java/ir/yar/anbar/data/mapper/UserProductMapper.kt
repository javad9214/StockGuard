package ir.yar.anbar.data.mapper

import ir.yar.anbar.data.local.entity.UserProductEntity
import ir.yar.anbar.data.remote.dto.CatalogProductDto
import ir.yar.anbar.data.remote.dto.CatalogStatus
import ir.yar.anbar.data.remote.dto.request.UserProductRequestDto
import ir.yar.anbar.data.remote.dto.response.UserProductResponseDto
import ir.yar.anbar.domain.model.Barcode
import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.model.ProductDescription
import ir.yar.anbar.domain.model.ProductId
import ir.yar.anbar.domain.model.ProductImage
import ir.yar.anbar.domain.model.ProductName
import ir.yar.anbar.domain.model.ProductTags
import ir.yar.anbar.domain.model.ProductUnit
import ir.yar.anbar.domain.model.StockQuantity
import ir.yar.anbar.domain.model.SubcategoryId
import ir.yar.anbar.domain.model.SupplierId
import ir.yar.anbar.domain.model.type.Money
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


fun UserProductEntity.toDomain(): Product {
    return Product(
        id = ProductId(id),
        name = ProductName(customName ?: name), // Use customName if exists, fallback to name
        barcode = barcode?.let { Barcode(it) },
        price = Money(price),
        costPrice = Money(costPrice),
        description = description?.let { ProductDescription(it) },
        image = if (imageLocalPath != null || imageUrl != null)
            ProductImage(localUri = imageLocalPath, remoteUrl = imageUrl)
        else null,
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

fun Product.toEntity(): UserProductEntity {
    return UserProductEntity(
        id = id.value,
        serverId = null, // Add this
        catalogProductId = null, // Add this
        name = name.value,
        barcode = barcode?.value,
        customName = null, // Add this
        price = price.amount,
        costPrice = costPrice.amount,
        description = description?.value,
        imageLocalPath = image?.localUri,
        imageUrl = image?.remoteUrl,
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
        syncStatus = "SYNCED", // Add this
        synced = synced,
        createdAt = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        updatedAt = updatedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        isDeleted = false
    )
}

fun Product.toRequestDto(): UserProductRequestDto {
    return UserProductRequestDto(
        catalogProductId = null,
        customName = name.value,
        price = price.amount,
        costPrice = costPrice.amount,
        description = description?.value,
        subcategoryId = subcategoryId?.value,
        supplierId = supplierId?.value,
        unit = unit?.value,
        stock = stock.value,
        minStockLevel = minStockLevel?.value,
        maxStockLevel = maxStockLevel?.value,
        isActive = isActive,
        tags = tags?.value
    )
}

/**
 * Merges server-authoritative fields of [UserProductResponseDto] into an existing local row.
 * Local-only fields the server does not know about (name, barcode, local image path,
 * local id, dates) are preserved.
 */
fun UserProductResponseDto.mergeInto(entity: UserProductEntity): UserProductEntity {
    val now = System.currentTimeMillis()
    return entity.copy(
        serverId = id,
        customName = customName,
        price = price,
        costPrice = costPrice,
        description = description,
        subcategoryId = subcategoryId,
        supplierId = supplierId,
        unit = unit,
        stock = stock,
        minStockLevel = minStockLevel,
        maxStockLevel = maxStockLevel,
        isActive = isActive,
        tags = tags,
        syncStatus = UserProductEntity.SYNC_STATUS_SYNCED,
        synced = true,
        updatedAt = runCatching {
            LocalDateTime.parse(updatedAt).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }.getOrDefault(now)
    )
}

fun CatalogProductDto.toDomain(): Product {
    return Product(
        id = ProductId(id),
        name = ProductName(name),
        barcode = barcode?.let { Barcode(it) },
        description = description?.let { ProductDescription(it) },
        image = imageUrl?.let { ProductImage(it) },

        // Catalog has no selling/cost price — default to zero or map suggestedPrice as needed
        price = suggestedPrice?.let { Money(it) } ?: Money(0),
        costPrice = Money(0),

        subcategoryId = subcategoryId?.let { SubcategoryId(it) },
        supplierId = null, // Catalog has no supplier concept

        unit = unit?.let { ProductUnit(it) },

        // Catalog has no local stock data
        stock = StockQuantity(0),
        minStockLevel = null,
        maxStockLevel = null,

        isActive = status == CatalogStatus.VERIFIED,
        tags = tags?.let { ProductTags(it) },
        lastSoldDate = null, // Not part of catalog

        date = LocalDateTime.parse(createdAt),
        createdAt = LocalDateTime.parse(createdAt),
        updatedAt = LocalDateTime.parse(updatedAt),

        synced = true,
    )
}