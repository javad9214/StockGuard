package ir.yar.anbar.data.mapper

import ir.yar.anbar.data.local.entity.CatalogProductEntity
import ir.yar.anbar.data.remote.dto.response.BarcodeProductResponseDto
import ir.yar.anbar.domain.model.BarcodeProduct

fun BarcodeProductResponseDto.toDomain(): BarcodeProduct = BarcodeProduct(
    catalogId = catalogId,
    name = name,
    imageUrl = imageUrl,
    sellPrice = sellPrice
)

fun CatalogProductEntity.toBarcodeDomain(): BarcodeProduct = BarcodeProduct(
    name = name,
    imageUrl = imageUrl,
    sellPrice = suggestedPrice
)
