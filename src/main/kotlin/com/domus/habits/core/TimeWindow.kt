package com.domus.habits.core

import java.time.DayOfWeek
import java.time.LocalDate

sealed interface TimeWindow {

    fun dateRange(from: LocalDate): Pair<LocalDate, LocalDate>

    data object Daily : TimeWindow {
        override fun dateRange(from: LocalDate) = from to from
    }

    data object Weekly : TimeWindow {
        override fun dateRange(from: LocalDate): Pair<LocalDate, LocalDate> {
            val monday = from.with(DayOfWeek.MONDAY)
            return monday to monday.plusDays(6)
        }
    }

    data object Monthly : TimeWindow {
        override fun dateRange(from: LocalDate): Pair<LocalDate, LocalDate> {
            return from.withDayOfMonth(1) to from.withDayOfMonth(from.lengthOfMonth())
        }
    }

    data class EveryNDays(val days: Int) : TimeWindow {
        override fun dateRange(from: LocalDate): Pair<LocalDate, LocalDate> {
            return from.minusDays(days.toLong() - 1) to from
        }
    }
}
