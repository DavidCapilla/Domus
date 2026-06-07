package com.domus.core.chore

sealed interface Schedule {
    data object OneTime : Schedule
    data class EveryNDays(val days: Int) : Schedule
}
