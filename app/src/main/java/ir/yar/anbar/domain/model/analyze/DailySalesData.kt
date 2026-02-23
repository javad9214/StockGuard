package ir.yar.anbar.domain.model.analyze

import java.time.LocalDate

data class DailySalesData(
    val date: LocalDate,
    val totalRevenue: Long,
    val totalCost: Long
) {
    fun getTotalProfit(): Long = totalRevenue - totalCost
}