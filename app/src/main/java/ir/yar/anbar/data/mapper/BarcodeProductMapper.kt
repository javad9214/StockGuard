package ir.yar.anbar.data.mapper

import ir.yar.anbar.data.remote.dto.response.BarcodeProductResponseDto
import ir.yar.anbar.domain.model.BarcodeProduct

fun BarcodeProductResponseDto.toDomain(): BarcodeProduct = BarcodeProduct(
    name = name,
    imageUrl = imageUrl,
    sellPrice = sellPrice
)
