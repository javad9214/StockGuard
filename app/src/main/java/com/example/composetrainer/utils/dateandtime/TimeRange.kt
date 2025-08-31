package com.example.composetrainer.utils.dateandtime

import com.example.composetrainer.utils.dateandtime.TimeStampUtil.getCurrentShamsiMonthStartEndMillis
import com.example.composetrainer.utils.dateandtime.TimeStampUtil.getCurrentWeekStartEndMillis
import com.example.composetrainer.utils.dateandtime.TimeStampUtil.getLastShamsiMonthStartEndMillis
import com.example.composetrainer.utils.dateandtime.TimeStampUtil.getLastWeekStartEndMillis
import com.example.composetrainer.utils.dateandtime.TimeStampUtil.getTodayStartEndMillis
import com.example.composetrainer.utils.dateandtime.TimeStampUtil.getYesterdayStartEndMillis
import java.util.Calendar

enum class TimeRange(val displayName: String) {
    TODAY("Today"),
    YESTERDAY("Yesterday"),
    THIS_WEEK("This Week"),
    LAST_WEEK("Last Week"),
    THIS_MONTH("This Month"),
    LAST_MONTH("Last Month"),
    THIS_YEAR("This Year"),
    LAST_YEAR("Last Year");

    fun getStartAndEndTimes(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        val now = calendar.clone() as Calendar
        val start = calendar.clone() as Calendar
        val end = calendar.clone() as Calendar

        when (this) {
            TODAY -> {
                return getTodayStartEndMillis()
            }

            YESTERDAY -> {
                return getYesterdayStartEndMillis()
            }

            THIS_WEEK -> {
                return getCurrentWeekStartEndMillis()
            }

            LAST_WEEK -> {
                return getLastWeekStartEndMillis()
            }

            THIS_MONTH -> {
                return getCurrentShamsiMonthStartEndMillis()
            }

            LAST_MONTH -> {
                return getLastShamsiMonthStartEndMillis()
            }

            THIS_YEAR -> {
                start.set(Calendar.MONTH, Calendar.JANUARY)
                start.set(Calendar.DAY_OF_MONTH, 1)
                start.set(Calendar.HOUR_OF_DAY, 0)
                start.set(Calendar.MINUTE, 0)
                start.set(Calendar.SECOND, 0)
                start.set(Calendar.MILLISECOND, 0)

                end.set(Calendar.MONTH, Calendar.DECEMBER)
                end.set(Calendar.DAY_OF_MONTH, 31)
                end.set(Calendar.HOUR_OF_DAY, 23)
                end.set(Calendar.MINUTE, 59)
                end.set(Calendar.SECOND, 59)
                end.set(Calendar.MILLISECOND, 999)

                return Pair(start.timeInMillis, end.timeInMillis)
            }

            LAST_YEAR -> {
                start.add(Calendar.YEAR, -1)
                start.set(Calendar.MONTH, Calendar.JANUARY)
                start.set(Calendar.DAY_OF_MONTH, 1)
                start.set(Calendar.HOUR_OF_DAY, 0)
                start.set(Calendar.MINUTE, 0)
                start.set(Calendar.SECOND, 0)
                start.set(Calendar.MILLISECOND, 0)

                end.add(Calendar.YEAR, -1)
                end.set(Calendar.MONTH, Calendar.DECEMBER)
                end.set(Calendar.DAY_OF_MONTH, 31)
                end.set(Calendar.HOUR_OF_DAY, 23)
                end.set(Calendar.MINUTE, 59)
                end.set(Calendar.SECOND, 59)
                end.set(Calendar.MILLISECOND, 999)

                return Pair(start.timeInMillis, end.timeInMillis)
            }

        }

    }
}