package com.domus.chores.core

sealed interface Schedule {
    data object OneTime : Schedule
    data class EveryNDays(val days: Int) : Schedule
}
