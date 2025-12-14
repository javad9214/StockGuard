package ir.yar.anbar.domain.usecase.sales

import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.model.ProductSalesSummary
import ir.yar.anbar.domain.model.SalesQuantity
import ir.yar.anbar.domain.model.type.Money
import ir.yar.anbar.domain.repository.ProductSalesSummaryRepository
import ir.yar.anbar.domain.usecase.product.GetProductsByIDsUseCase
import ir.yar.anbar.utils.dateandtime.TimeRange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTopSellingProductsUseCase @Inject constructor(
    private val productSalesSummaryRepository: ProductSalesSummaryRepository,
    private val getProductsByIDsUseCase: GetProductsByIDsUseCase
) {

    operator fun invoke(timeRange: TimeRange): Flow<Pair<List<ProductSalesSummary>, List<Product>>> {
        val (startTime, endTime) = timeRange.getStartAndEndTimes()

        
        val summariesFlow = productSalesSummaryRepository.getTopSellingProductsBetween(startTime, endTime)

        return summariesFlow.map { summaries ->

            // Group by productId and sum the values
            val aggregatedSummaries = summaries
                .groupBy { it.productId }
                .map { (productId, productSummaries) ->
                    val baseSummary = productSummaries.first()
                    val totalSold = productSummaries.sumOf { it.totalSold.value }
                    val totalRevenue = productSummaries.sumOf { it.totalRevenue.amount }
                    val totalCost = productSummaries.sumOf { it.totalCost.amount }
                    val earliestCreated = productSummaries.minOf { it.createdAt }
                    val latestUpdated = productSummaries.maxOf { it.updatedAt }
                    val earliestDate = productSummaries.minOf { it.date }
                    val allSynced = productSummaries.all { it.synced }

                    baseSummary.copy(
                        date = earliestDate,
                        totalSold = SalesQuantity(totalSold),
                        totalRevenue = Money(totalRevenue),
                        totalCost = Money(totalCost),
                        createdAt = earliestCreated,
                        updatedAt = latestUpdated,
                        synced = allSynced
                    )
                }
                .sortedByDescending { it.totalSold.value }


            // Fetch product details for these IDs (one-shot suspend)
            val productIds = getProductsByIDsUseCase.invoke(aggregatedSummaries.map { it.productId.value })

            // Emit a single pair for each emission from the repository
            aggregatedSummaries to productIds
        }
    }


}