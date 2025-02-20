package com.github.wyyzlxg.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * @author wangyu
 *
 */
object DateUtils {

    /**
     * 通过指定的年份和月份获取当月有多少天.
     *
     * @param year  年.
     * @param month 月.
     * @return 天数.
     */
    fun getMonthDays(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance(Locale.CHINA)
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    /**
     * 获取指定年月的 1 号位于周几.
     *
     * @param year  年.
     * @param month 月.
     * @return      周.
     */
    fun getFirstDayWeek(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance(Locale.CHINA)
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        return calendar.get(Calendar.DAY_OF_WEEK)
    }

    /**
     * 获取指定年月的最后一天位于周几.
     *
     * @param year  年.
     * @param month 月.
     * @return      周.
     */
    fun getLastDayWeek(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance(Locale.CHINA)
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        return calendar.get(Calendar.DAY_OF_WEEK)
    }

    /**
     * 根据格式化后的日期和相应格式化格式返回时间毫秒值
     *
     * @param formatDate 格式化后的日期.
     * @param pattern    格式化格式.
     */
    fun getTimeMillisWithPattern(formatDate: String, pattern: String): Long {
        val sdf = SimpleDateFormat(pattern, Locale.CHINA)
        val date = sdf.parse("$formatDate 00:00:00")
        val calendar = Calendar.getInstance(Locale.CHINA)
        calendar.time = date!!
        return calendar.timeInMillis
    }
}