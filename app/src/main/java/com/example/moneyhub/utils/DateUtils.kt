package com.example.moneyhub.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtils {
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // 날짜 -> 밀리초
    fun dateToMillis(dateStr: String): Long? {
        return try {
            sdf.parse(dateStr)?.time
        } catch (e: Exception) {
            null
        }
    }

    // 밀리초 -> 날짜
    fun millisToDate(timeMillis: Long): String {
        return Instant.ofEpochMilli(timeMillis)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    fun millisToLocalDateTime(timeMillis: Long): LocalDateTime {
        val instant = Instant.ofEpochMilli(timeMillis)
        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

        return dateTime
    }

    // timestamp의 자정(00:00:00.000)을 반환
    fun getStartOfDay(timestamp: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    // timestamp의 해당 일의 마지막 시간(23:59:59.999)을 반환
    fun getEndOfDay(timestamp: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }

    // timestamp의 해당 월의 첫 날 자정을 반환
    fun getStartOfMonth(timestamp: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun getEndOfMonth(timestamp: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }


    // 두 타임 스탬프가 같은 날짜인지 확인하는 함수

    // 캘린더에서 12월 8일을 클릭했다면,
    // 시간과는 상관없이 12월 8일의 모든 거래 내역을 조회해야 한다.
    // 이때 isSameDay 함수를 사용하여 선택된 날짜와 각 거래 내역의 날짜를 비교하여 필터링 한다.
    fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
        // timestamp1을 Calendar 객체로 변환
        val cal1 = Calendar.getInstance().apply{
            timeInMillis = timestamp1
        }
        // timestamp1을 Calendar 객체로 변환
        val cal2 = Calendar.getInstance().apply {
            timeInMillis = timestamp2
        }

        // 년, 월, 일이 모두 같은지 확인
        // 년, 월, 일이 모두 같은지 확인
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }
}