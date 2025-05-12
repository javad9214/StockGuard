package com.example.composetrainer.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateFormatter {
    fun formatDate(timestamp: Long): String {
        val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        return formatter.format(Date(timestamp))
    }

    fun getCurrentTimeFormatted(): String {
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        return formatter.format(Date())
    }

    fun getHijriShamsiDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val persianDate = gregorianToShamsi(year, month, day)
        return "${persianDate[2]}/${persianDate[1]}/${persianDate[0]}"
    }

    fun getFormattedHijriShamsiDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val persianDate = gregorianToShamsi(year, month, day)
        return "${persianDate[0]}/${String.format("%02d", persianDate[1])}/${
            String.format(
                "%02d",
                persianDate[2]
            )
        }"
    }

    /**
     * Converts Gregorian date to Hijri Shamsi (Persian) date
     * @return Array of [year, month, day]
     */
    private fun gregorianToShamsi(year: Int, month: Int, day: Int): Array<Int> {
        val breaks = intArrayOf(
            -61,
            9,
            38,
            199,
            426,
            686,
            756,
            818,
            1111,
            1181,
            1210,
            1635,
            2060,
            2097,
            2192,
            2262,
            2324,
            2394,
            2456,
            3178
        )

        val gregorianYear = year - 1600
        val gregorianMonth = month - 1
        val gregorianDay = day - 1

        val gregorianDayNo = 365 * gregorianYear + gregorianYear / 4 - gregorianYear / 100 +
                gregorianYear / 400 + (gregorianMonth * 30 + gregorianMonth / 2) + gregorianDay

        var persianDayNo = gregorianDayNo - 79
        var persianNP = persianDayNo / 12053
        persianDayNo %= 12053

        var persianYear = 979 + 33 * persianNP + 4 * (persianDayNo / 1461)
        persianDayNo %= 1461

        if (persianDayNo >= 366) {
            persianYear += (persianDayNo - 1) / 365
            persianDayNo = (persianDayNo - 1) % 365
        }

        val persianMonth: Int
        val persianDay: Int

        if (persianDayNo < 186) {
            persianMonth = 1 + persianDayNo / 31
            persianDay = 1 + persianDayNo % 31
        } else {
            persianDayNo -= 186
            persianMonth = 7 + persianDayNo / 30
            persianDay = 1 + persianDayNo % 30
        }

        return arrayOf(persianYear, persianMonth, persianDay)
    }
}