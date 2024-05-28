package cn.jingzhuan.lib.chart3.formatter

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateTimeFormatter {

    private val dateFormatters = mutableMapOf<String, DateTimeFormatter>()

    fun ofPattern(pattern: String): DateTimeFormatter {
        return dateFormatters[pattern] ?: DateTimeFormatter.ofPattern(pattern, Locale.CHINA)
    }

    /**
     * 仅支持精确到日期（不支持时分秒，性能比[formatTime]稍高）
     */
    fun DateTimeFormatter.formatDate(mill: Long): String {
        return format(LocalDate.ofEpochDay(TimeUnit.MILLISECONDS.toDays(mill)))
    }

    /**
     * 精确到秒
     */
    fun DateTimeFormatter.formatTime(mill: Long): String {
        return format(LocalDateTime.ofInstant(Instant.ofEpochMilli(mill), ZoneId.of("UTC+8")))
    }
}