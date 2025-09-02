package com.example.composetrainer.utils.dateandtime

import saman.zamani.persiandate.PersianDate
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar


object TimeStampUtil {

    fun getStartOfCurrentHour(): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    fun toDateTime(timestamp: Long): LocalDate {
         return  Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }

    fun getStartOfCurrentHourDateTime(): LocalDateTime {
        return LocalDateTime.now(ZoneId.systemDefault()).withMinute(0).withSecond(0).withNano(0)
    }


    // General
    fun getTodayAsTimestamp(): Long {
        return System.currentTimeMillis()
    }

    fun getTodayStartEndMillis(): Pair<Long, Long> {
        val today = PersianDate()
        val startOfDay = PersianDate().apply {
            setShDay(today.shDay)
            setShMonth(today.shMonth)
            setShYear(today.shYear)
            setHour(0)
            setMinute(0)
            setSecond(0)
        }

        val endOfDay = PersianDate().apply {
            setShDay(today.shDay)
            setShMonth(today.shMonth)
            setShYear(today.shYear)
            setHour(23)
            setMinute(59)
            setSecond(59)
        }

        return startOfDay.time to endOfDay.time
    }

    fun getYesterdayStartEndMillis(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()

        // Set the calendar to yesterday
        calendar.add(Calendar.DAY_OF_YEAR, -1)

        // Set to the start of the day (midnight)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val startOfYesterday = calendar.timeInMillis // Start timestamp of yesterday

        // Move to the end of the day (23:59:59.999)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)

        val endOfYesterday = calendar.timeInMillis // End timestamp of yesterday

        return Pair(startOfYesterday, endOfYesterday) // Return the pair of timestamps
    }

    // Week

    fun getCurrentWeekStartEndMillis(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()

        // Set the calendar to the first day of the week (Saturday)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
        calendar.add(Calendar.WEEK_OF_YEAR, -1) // Move to the saturday of the current week
        // Set the time to the start of the day
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val startOfWeek = calendar.timeInMillis // Start of the week timestamp
        val currentTime = System.currentTimeMillis() // Current time timestamp

        return Pair(startOfWeek, currentTime) // Return the pair of timestamps
    }

    fun getLastWeekStartEndMillis(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()

        // Set the calendar to the last Saturday
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
        calendar.add(Calendar.WEEK_OF_YEAR, -2) // Move to the last week
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val startOfLastWeek = calendar.timeInMillis // Start of last week timestamp

        val endWeek = Calendar.getInstance()

        // Set the calendar to the end of last week (Friday)
        endWeek.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
        endWeek.add(Calendar.WEEK_OF_YEAR, -1) // Move to the following Friday
        endWeek.set(Calendar.HOUR_OF_DAY, 23)
        endWeek.set(Calendar.MINUTE, 59)
        endWeek.set(Calendar.SECOND, 59)
        endWeek.set(Calendar.MILLISECOND, 0)
        val endOfLastWeek = endWeek.timeInMillis // End of last week timestamp

        return Pair(startOfLastWeek, endOfLastWeek) // Return the pair of timestamps
    }

    // Month

    fun getCurrentShamsiMonthStartEndMillis(): Pair<Long, Long> {
        val now = PersianDate()

        // Start of current month
        val startOfMonth = PersianDate()
        startOfMonth.setShYear(now.shYear)
        startOfMonth.setShMonth(now.shMonth)
        startOfMonth.setShDay(1)
        startOfMonth.setHour(0)
        startOfMonth.setMinute(0)
        startOfMonth.setSecond(0)

        // End of current month
        val endOfMonth = PersianDate()
        endOfMonth.setShYear(now.shYear)
        endOfMonth.setShMonth(now.shMonth)
        endOfMonth.setShDay(now.monthDays) // Gets number of days in this month
        endOfMonth.setHour(23)
        endOfMonth.setMinute(59)
        endOfMonth.setSecond(59)

        return startOfMonth.time to endOfMonth.time
    }

    fun getLastShamsiMonthStartEndMillis(): Pair<Long, Long> {
        val now = PersianDate()
        val lastMonth = if (now.shMonth == 1) 12 else now.shMonth - 1
        val yearOfLastMonth = if (now.shMonth == 1) now.shYear - 1 else now.shYear

        // Start of last month
        val startOfLastMonth = PersianDate()
        startOfLastMonth.setShYear(yearOfLastMonth)
        startOfLastMonth.setShMonth(lastMonth)
        startOfLastMonth.setShDay(1)
        startOfLastMonth.setHour(0)
        startOfLastMonth.setMinute(0)
        startOfLastMonth.setSecond(0)

        // End of last month
        val endOfLastMonth = PersianDate()
        endOfLastMonth.setShYear(yearOfLastMonth)
        endOfLastMonth.setShMonth(lastMonth)
        endOfLastMonth.setShDay(startOfLastMonth.monthDays)
        endOfLastMonth.setHour(23)
        endOfLastMonth.setMinute(59)
        endOfLastMonth.setSecond(59)

        return startOfLastMonth.time to endOfLastMonth.time
    }



}