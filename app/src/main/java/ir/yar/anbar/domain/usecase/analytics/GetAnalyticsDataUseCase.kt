package ir.yar.anbar.domain.usecase.analytics

import ir.yar.anbar.domain.model.AnalyticsData
import ir.yar.anbar.domain.model.MonthlySummary
import ir.yar.anbar.domain.repository.InvoiceRepository
import saman.zamani.persiandate.PersianDate
import javax.inject.Inject

class GetAnalyticsDataUseCase @Inject constructor(
    private val invoiceRepository: InvoiceRepository
) {
    suspend operator fun invoke(): AnalyticsData {
        val currentYearMonth = getCurrentYearMonth()

        val totalSales = invoiceRepository.getTotalSalesForMonth(currentYearMonth)
        val invoiceCount = invoiceRepository.getTotalInvoicesForMonth(currentYearMonth)
        val totalQuantity = invoiceRepository.getTotalQuantityForMonth(currentYearMonth)
        val topSellingProducts = invoiceRepository.getTopSellingProductsForMonth(currentYearMonth)

        val monthlySummary = MonthlySummary(
            totalSales = totalSales,
            invoiceCount = invoiceCount,
            totalQuantity = totalQuantity
        )

        return AnalyticsData(
            monthlySummary = monthlySummary,
            topSellingProducts = topSellingProducts
        )
    }

    private fun getCurrentYearMonth(): String {
        val persianDate = PersianDate()
        val year = persianDate.shYear
        val month = persianDate.shMonth.toString().padStart(2, '0')
        return "$year/$month"  // Slash-separated to match the database format
    }
}
