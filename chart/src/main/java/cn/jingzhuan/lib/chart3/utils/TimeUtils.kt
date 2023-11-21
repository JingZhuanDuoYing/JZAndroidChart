package cn.jingzhuan.lib.chart3.utils

import java.time.LocalDate
import java.util.*
import java.util.concurrent.TimeUnit

object TimeUtils {

    fun isInSameWeek(
        millis1: Long,
        millis2: Long
    ): Boolean {
        val week1 = getWeek(millis1)
        val week2 = getWeek(millis2)
        return week1 == week2
    }
    fun isInSameWeek(
        seconds1: Int,
        seconds2: Int
    ) = isInSameWeek(seconds1 * 1000L, seconds2 * 1000L)
    fun getWeek(millis: Long) = getWeek((millis / 1000L).toInt())
    fun getWeek(seconds: Int): Int {
        /** 1970年1月1日 星期四 这天的下一周从三天（72小时）后的周日开始**/
        return (seconds + 8 * 3600 + 72 * 3600) / (60 * 60 * 24 * 7)
    }

    fun isInSameMonth(
        millis1: Long,
        millis2: Long
    ): Boolean {
        if (!isInSameYear(millis1, millis2)) return false
        return getMonth(millis1) == getMonth(millis2)
    }
    fun isInSameMonth(
        seconds1: Int,
        seconds2: Int
    ) = isInSameMonth(seconds1 * 1000L, seconds2 * 1000L)

    fun getMonth(millis: Long) = getMonth((millis / 1000L).toInt())
    fun getMonth(seconds: Int): Int {
        val timeZoneShift = TimeUnit.HOURS.toSeconds(8)
        return LocalDate.ofEpochDay(TimeUnit.SECONDS.toDays(seconds + timeZoneShift)).monthValue
    }
    fun getDayOfMonth(millis: Long) = getDayOfMonth((millis / 1000L).toInt())
    fun getDayOfMonth(seconds: Int): Int {
        val timeZoneShift = TimeUnit.HOURS.toSeconds(8)
        return LocalDate.ofEpochDay(TimeUnit.SECONDS.toDays(seconds + timeZoneShift)).dayOfMonth
    }

    fun isInSameQuarter(seconds1: Int, seconds2: Int) = isInSameQuarter(seconds1 * 1000L, seconds2 * 1000L)
    fun isInSameQuarter(
        millis1: Long,
        millis2: Long
    ): Boolean {
        if (!isInSameYear(millis1, millis2)) return false
        val firstMonth = LocalDate.ofEpochDay(TimeUnit.MILLISECONDS.toDays(millis1)).monthValue
        val secondMonth = LocalDate.ofEpochDay(TimeUnit.MILLISECONDS.toDays(millis2)).monthValue
        return when (firstMonth) {
            in 1..3 -> secondMonth in 1..3
            in 4..6 -> secondMonth in 4..6
            in 7..9 -> secondMonth in 7..9
            in 10..12 -> secondMonth in 10..12
            else -> false
        }
    }

    fun getQuarter(millis: Long) = getQuarter((millis / 1000L).toInt())
    fun getQuarter(seconds: Int): Int {
        val timeZoneShift = TimeUnit.HOURS.toSeconds(8)
        return when (LocalDate.ofEpochDay(TimeUnit.SECONDS.toDays(seconds + timeZoneShift)).monthValue) {
            in 1..3 -> 1
            in 4..6 -> 2
            in 7..9 -> 3
            in 10..12 -> 4
            else -> -1
        }
    }

    fun isInSameYear(
        millis1: Long,
        millis2: Long
    ): Boolean {
        val year1 = getYear(millis1)
        val year2 = getYear(millis2)
        return year1 == year2
    }
    fun isInSameYear(
        seconds1: Int,
        seconds2: Int
    ) = isInSameYear(seconds1 * 1000L, seconds2 * 1000L)

    /**
     * 如何高效判断日期是否是同一年
     * 先将时间戳转换为该时区的天数，再通过每年365天猜测一个年份，算出该年份的天数，然后用实际天数减去猜测的年份的天数，
     * 计算出剩余的天数，如果剩余天数少于该猜测的年份的天数（365或者366）则退出循环，返回该猜测的年份
     * 如果剩余天数大于该猜测的年份的天数（365或者366），则再对剩余的天数进行同样的处理。
     */
    fun getYear(seconds: Int) = getYear(seconds * 1000L)

    fun getYear(millis: Long): Int {
        val timeZoneShift = TimeUnit.HOURS.toMillis(8)
        var days = TimeUnit.MILLISECONDS.toDays(millis + timeZoneShift)
        var year = 1970
        // 对于时间戳为负也处理
        while (days < 0 || days >= (if (isLeapYear(year)) 366 else 365)) {
            var guessYear = 0
            guessYear = if (days >= 0) {
                year + (days / 365).toInt()
            } else {
                year - ((kotlin.math.abs(days) / 365).toInt() + 1)
            }
            days -= (guessYear - year) * 365 + leapsEndOfYear(guessYear - 1) - leapsEndOfYear(year - 1)
            year = guessYear
        }
        return year
    }

    // 获取该年份前所有闰年数
    private fun leapsEndOfYear(year:Int): Int {
        return year/4 - year/100 + year/400
    }
    // 判断闰年(能被4整除，但不能被100整除；或者能被400整除的年份)
    private fun isLeapYear(year:Int): Boolean {
        return ((year % 4 == 0) && ((year % 100 != 0) || (year % 400 == 0)))
    }

    // </editor-fold desc="判断方法">    ---------------------------------------------------------

    /**
     * 判断是否为同一分钟
     */
    fun isSameMinutes(
        millis1: Long,
        millis2: Long
    ): Boolean {
        return TimeUnit.MILLISECONDS.toMinutes(millis1) == TimeUnit.MILLISECONDS.toMinutes(millis2)
    }

    fun isInSameMinutesRange(
        millis1: Long,
        millis2: Long,
        range: Int = 1
    ): Boolean {
        if (!isSameDay(millis1, millis2)) return false

        val firstMinutes = TimeUnit.MILLISECONDS.toMinutes(millis1)
        val secondMinutes = TimeUnit.MILLISECONDS.toMinutes(millis2)
        if ((firstMinutes - secondMinutes) >= range) return false

        val seconds1 = TimeUnit.MILLISECONDS.toSeconds(millis1)
        val openSecond = (seconds1 - (seconds1 + 28800) % 86400) + (9 * 3600 + 1800) // 当天开盘时间（秒）
        val openMinute = openSecond / 60

        return (firstMinutes - openMinute).toInt() / range == (secondMinutes - openMinute).toInt() / range
    }

    /**
     * 判断两个时间戳是否为同一天
     */
    fun isSameDayOfMillis(
        ms1: Long,
        ms2: Long
    ): Boolean {
        return isSameDay(ms1, ms2)
    }

    /**
     * 判断是否为同一天
     */
    fun isSameDay(
        millis1: Long,
        millis2: Long
    ): Boolean {
        return getDay(millis1) == getDay(millis2)
    }
    fun isSameDay(
        seconds1: Int,
        seconds2: Int
    ) = isSameDay(seconds1 * 1000L, seconds2 * 1000L)

    fun getDay(millis: Long) = getDay((millis / 1000L).toInt())
    fun getDay(seconds: Int): Int {
        val timeZoneShift = TimeUnit.HOURS.toSeconds(8)
        return (TimeUnit.SECONDS.toDays(seconds + timeZoneShift)).toInt()
    }

    fun isInSameCycle(
        millis1: Long,
        millis2: Long,
        cycle: Int = 8
    ): Boolean {
        return when(cycle) {
            -1 -> isSameMinutes(millis1, millis2)
            -2 -> isSameMinutes(millis1, millis2)
            0 -> isSameMinutes(millis1, millis2)
            1 -> isInSameMinutesRange(millis1, millis2, 3)
            2 -> isInSameMinutesRange(millis1, millis2, 5)
            3 -> isInSameMinutesRange(millis1, millis2, 10)
            4 -> isInSameMinutesRange(millis1, millis2, 15)
            5 -> isInSameMinutesRange(millis1, millis2, 20)
            6 -> isInSameMinutesRange(millis1, millis2, 30)
            7 -> isInSameMinutesRange(millis1, millis2, 60)
            13 -> isInSameMinutesRange(millis1, millis2, 120)
            8 -> isSameDay(millis1, millis2)
            9 -> isInSameWeek(millis1, millis2)
            10 -> isInSameMonth(millis1, millis2)
            11 -> isInSameQuarter(millis1, millis2)
            12 -> isInSameYear(millis1, millis2)
            else -> false
        }
    }

    /**
     * 获取当天的最后一分钟
     */
    fun getDayOfLastMinute(
        millis: Long,
    ): Long {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"))
        calendar.timeInMillis = millis
        calendar[Calendar.SECOND] = 59
        calendar[Calendar.MINUTE] = 59
        calendar[Calendar.HOUR_OF_DAY] = 23

        return calendar.timeInMillis
    }

}