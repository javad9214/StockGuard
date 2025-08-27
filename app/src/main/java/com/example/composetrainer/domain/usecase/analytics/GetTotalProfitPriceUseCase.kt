package com.example.composetrainer.domain.usecase.analytics

import com.example.composetrainer.domain.repository.InvoiceRepository
import javax.inject.Inject

class GetTotalProfitPriceUseCase @Inject constructor(
    private val invoiceRepository: InvoiceRepository
) {
}