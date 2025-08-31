package com.example.composetrainer.domain.usecase.sales

import com.example.composetrainer.domain.model.ProductSalesSummary
import com.example.composetrainer.domain.model.SalesQuantity
import com.example.composetrainer.utils.dateandtime.TimeRange
import com.example.composetrainer.domain.model.type.Money
import com.example.composetrainer.domain.repository.ProductSalesSummaryRepository
import javax.inject.Inject

class GetProductSalesSummaryUseCase @Inject constructor(
    private val productSalesSummaryRepository: ProductSalesSummaryRepository
) {

    suspend operator fun invoke(timeRange: TimeRange): List<ProductSalesSummary> {
        val (startTime, endTime) = timeRange.getStartAndEndTimes()
        val summaries = productSalesSummaryRepository.getTopSellingProductsBetween(startTime, endTime)

        // Group by productId and sum the values
        val aggregatedSummaries = summaries
            .groupBy { it.productId }
            .map { (productId, productSummaries) ->
                // Take the first summary as base and aggregate the rest
                val baseSummary = productSummaries.first()

                // Sum all the financial and quantity data
                val totalSold = productSummaries.sumOf { it.totalSold.value }
                val totalRevenue = productSummaries.sumOf { it.totalRevenue.amount }
                val totalCost = productSummaries.sumOf { it.totalCost.amount }

                // Find the earliest and latest dates for created/updated times
                val earliestCreated = productSummaries.minOf { it.createdAt }
                val latestUpdated = productSummaries.maxOf { it.updatedAt }

                // Use the earliest date from the summaries
                val earliestDate = productSummaries.minOf { it.date }

                // Check if all summaries are synced
                val allSynced = productSummaries.all { it.synced }

                // Create aggregated summary
                baseSummary.copy(
                    date = earliestDate, // or you might want to use a date range
                    totalSold = SalesQuantity(totalSold),
                    totalRevenue = Money(totalRevenue),
                    totalCost = Money(totalCost),
                    createdAt = earliestCreated,
                    updatedAt = latestUpdated,
                    synced = allSynced
                )
            }
            // Order by total revenue in descending order (you can change this criteria)
            .sortedByDescending { it.totalSold.value }

        return aggregatedSummaries
    }


}