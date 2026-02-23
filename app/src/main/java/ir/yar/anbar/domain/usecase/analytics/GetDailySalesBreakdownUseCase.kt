package ir.yar.anbar.domain.usecase.analytics

import ir.yar.anbar.domain.model.analyze.DailySalesData
import ir.yar.anbar.domain.repository.ProductSalesSummaryRepository
import ir.yar.anbar.utils.dateandtime.TimeRange
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDailySalesBreakdownUseCase @Inject constructor(
    private val productSalesSummaryRepository: ProductSalesSummaryRepository
) {
    operator fun invoke(timeRange: TimeRange): Flow<List<DailySalesData>> {
        val (startTime, endTime) = timeRange.getStartAndEndTimes()
        return productSalesSummaryRepository.getDailySalesBetween(startTime, endTime)
    }
}