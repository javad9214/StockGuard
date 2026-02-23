package ir.yar.anbar.domain.model

data class MonthlySummary(
    val totalSales: Long,
    val invoiceCount: Int,
    val totalQuantity: Int
)

data class TopSellingProductInfo(
    val name: String,
    val totalQuantity: Int,
    val totalSales: Long
)

data class AnalyticsData(
    val monthlySummary: MonthlySummary,
    val topSellingProducts: List<TopSellingProductInfo>
)