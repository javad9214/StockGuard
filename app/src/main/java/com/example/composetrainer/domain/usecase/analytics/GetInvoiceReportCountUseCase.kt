package com.example.composetrainer.domain.usecase.analytics

import com.example.composetrainer.domain.repository.InvoiceRepository
import com.example.composetrainer.utils.dateandtime.TimeStampUtil.getCurrentShamsiMonthStartEndMillis
import com.example.composetrainer.utils.dateandtime.TimeStampUtil.getCurrentWeekStartEndMillis
import com.example.composetrainer.utils.dateandtime.TimeStampUtil.getLastShamsiMonthStartEndMillis
import com.example.composetrainer.utils.dateandtime.TimeStampUtil.getLastWeekStartEndMillis
import com.example.composetrainer.utils.dateandtime.TimeStampUtil.getTodayStartEndMillis
import com.example.composetrainer.utils.dateandtime.TimeStampUtil.getYesterdayStartEndMillis
import javax.inject.Inject

class GetInvoiceReportCountUseCase @Inject constructor(
    private val invoiceRepository: InvoiceRepository
) {

    suspend fun getTodayInvoiceCount(): Int {
        val (start, end) = getTodayStartEndMillis()

        return invoiceRepository.getTotalInvoicesBetweenDates(start, end)
    }

    suspend fun getYesterdayInvoiceCount(): Int {
        val (start, end) = getYesterdayStartEndMillis()

        return invoiceRepository.getTotalInvoicesBetweenDates(start, end)
    }

    suspend fun getThisWeekInvoiceCount(): Int {
        val (start, end) = getCurrentWeekStartEndMillis()

        return invoiceRepository.getTotalInvoicesBetweenDates(start, end)
    }

    suspend fun getLastWeekInvoiceCount(): Int {
        val (start, end) = getLastWeekStartEndMillis()

        return invoiceRepository.getTotalInvoicesBetweenDates(start, end)
    }

    suspend fun getCurrentMonthInvoiceCount(): Int {
        val (start, end) = getCurrentShamsiMonthStartEndMillis()
        return invoiceRepository.getTotalInvoicesBetweenDates(start, end)
    }

    suspend fun getLastMonthInvoiceCount(): Int {
        val (start, end) = getLastShamsiMonthStartEndMillis()
        return invoiceRepository.getTotalInvoicesBetweenDates(start, end)
    }

    suspend fun getCustomRangeInvoiceCount(startMillis: Long, endMillis: Long): Int {
        return invoiceRepository.getTotalInvoicesBetweenDates(startMillis, endMillis)
    }

    companion object {
        private const val TAG = "GetInvoiceReportCountUseCase"
    }
}