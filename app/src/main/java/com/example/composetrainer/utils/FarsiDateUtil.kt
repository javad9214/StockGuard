package com.example.composetrainer.utils


import saman.zamani.persiandate.PersianDate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FarsiDateUtil {
    fun isLeapYear(year: Int): Boolean {
        val a = year - 474
        val b = a % 2820 + 474
        return (b * 682 % 2816) < 682 && b % 33 != 0
    }

    private val persianDaysOfWeek = arrayOf(
        "شنبه", "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنجشنبه", "جمعه"
    )
    private val persianMonths = arrayOf(
        "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور", "مهر",
        "آبان", "آذر", "دی", "بهمن", "اسفند"
    )

    fun getDayInMonth(month: Int, year: Int): Int{
        val daysInMonth = when (month) {
            1, 2, 3, 4, 5, 6 -> 31
            7, 8, 9, 10, 11 -> 30
            12 -> if (isLeapYear(year)) 30 else 29
            else -> 30
        }
        return daysInMonth
    }

    fun getDayOfWeek(year: Int, month: Int, day: Int): String {

        val persianDate = PersianDate()
        persianDate.setShYear(year)
        persianDate.setShMonth(month)
        persianDate.setShDay(day)

        val dayOfWeekIndex = persianDate.dayOfWeek()

        return persianDaysOfWeek[dayOfWeekIndex] // Adjusting for 0-indexed array
    }

    private fun getTodayPersianDateTriple(): Triple<Int, Int, Int> {
        val persianDate = PersianDate()
        val persianYear = persianDate.shYear
        val persianMonth = persianDate.shMonth // Month is 1-indexed (Farvardin = 1)
        val persianDay = persianDate.shDay

        return Triple(persianYear, persianMonth, persianDay)
    }

    fun getTodayPersianDate(): String {
        val (year, month, day) = getTodayPersianDateTriple()

        // Format month and day to be two digits
        val formattedMonth = month.toString().padStart(2, '0')
        val formattedDay = day.toString().padStart(2, '0')

        return "$year/$formattedMonth/$formattedDay"
    }

    fun getFormattedDate(dayOfWeek: String, day: Int, month: Int, year: Int): String {
        return String.format(
            Locale("fa", "IR"),
            "%s  %02d  %s  %d",
            dayOfWeek,
            day,
            persianMonths[month - 1], // Month is 1-indexed
            year
        )
    }

    fun getCurrentTimeFormatted(): String {
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        return formatter.format(Date())
    }

}