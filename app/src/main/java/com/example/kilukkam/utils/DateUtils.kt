package com.example.kilukkam.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class MonthYear(
    val year: Int,
    val month: Int // 0-based: 0 = January, 11 = December
) : Comparable<MonthYear> {
    
    fun getDisplayName(): String {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val format = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        return format.format(cal.time)
    }

    fun getShortName(): String {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val format = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        return format.format(cal.time)
    }

    fun getMonthShortName(): String {
        val cal = Calendar.getInstance().apply {
            set(Calendar.MONTH, month)
        }
        val format = SimpleDateFormat("MMM", Locale.getDefault())
        return format.format(cal.time)
    }

    fun previous(): MonthYear {
        return if (month == 0) {
            MonthYear(year - 1, 11)
        } else {
            MonthYear(year, month - 1)
        }
    }

    fun next(): MonthYear {
        return if (month == 11) {
            MonthYear(year + 1, 0)
        } else {
            MonthYear(year, month + 1)
        }
    }

    fun startTimestamp(): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    fun endTimestamp(): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return cal.timeInMillis
    }

    fun isCurrentMonth(): Boolean {
        val now = now()
        return this.year == now.year && this.month == now.month
    }

    override fun compareTo(other: MonthYear): Int {
        return if (this.year != other.year) {
            this.year.compareTo(other.year)
        } else {
            this.month.compareTo(other.month)
        }
    }

    companion object {
        fun now(): MonthYear {
            val cal = Calendar.getInstance()
            return MonthYear(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
        }

        fun fromTimestamp(timestamp: Long): MonthYear {
            val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
            return MonthYear(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
        }
    }
}

object DateUtils {
    fun getStartOfYear(year: Int): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, 0)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    fun getEndOfYear(year: Int): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, 11)
            set(Calendar.DAY_OF_MONTH, 31)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return cal.timeInMillis
    }

    fun getAvailableYears(timestamps: List<Long>): List<Int> {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        if (timestamps.isEmpty()) return listOf(currentYear)
        val cal = Calendar.getInstance()
        val years = timestamps.map {
            cal.timeInMillis = it
            cal.get(Calendar.YEAR)
        }.toMutableSet()
        years.add(currentYear)
        return years.sortedDescending()
    }

    fun getAvailableMonths(timestamps: List<Long>): List<MonthYear> {
        val currentMonth = MonthYear.now()
        val cal = Calendar.getInstance()
        val months = timestamps.map {
            cal.timeInMillis = it
            MonthYear(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
        }.toMutableSet()
        months.add(currentMonth)
        return months.sortedDescending()
    }
}

