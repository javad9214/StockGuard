package ir.yar.anbar.domain.usecase.sales

import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.model.ProductSalesSummary
import ir.yar.anbar.domain.model.aggregateByProduct
import ir.yar.anbar.domain.repository.ProductSalesSummaryRepository
import ir.yar.anbar.domain.usecase.product.GetProductsByIDsUseCase
import ir.yar.anbar.utils.dateandtime.TimeRange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTopProfitableProductsUseCase @Inject constructor(
    private val productSalesSummaryRepository: ProductSalesSummaryRepository,
    private val getProductsByIDsUseCase: GetProductsByIDsUseCase
) {
    operator fun invoke(timeRange: TimeRange): Flow<Pair<List<ProductSalesSummary>, List<Product>>> {
        val (startTime, endTime) = timeRange.getStartAndEndTimes()

        return productSalesSummaryRepository.getTopProfitableProductsBetween(startTime, endTime)
            .map { summaries ->
                val aggregatedSummaries = summaries
                    .aggregateByProduct()
                    .sortedByDescending { it.getTotalProfit().amount }

                val products = getProductsByIDsUseCase.invoke(
                    aggregatedSummaries.map { it.productId.value }
                )

                aggregatedSummaries to products
            }
    }
}
