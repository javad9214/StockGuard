package ir.yar.anbar.domain.usecase.analytics

import ir.yar.anbar.domain.repository.InvoiceRepository
import ir.yar.anbar.utils.dateandtime.TimeRange
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTotalSoldPriceUseCase @Inject constructor(
    private val invoiceRepository: InvoiceRepository
) {

    operator fun invoke(timeRange: TimeRange): Flow<Long> {
        val (start, end) = timeRange.getStartAndEndTimes()
        return invoiceRepository.getTotalSalesBetweenDates(start, end)
    }

}