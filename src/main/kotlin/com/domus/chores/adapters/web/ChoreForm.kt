package com.domus.chores.adapters.web

import com.domus.chores.adapters.web.dto.AreaDto
import com.domus.chores.core.Schedule
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class ChoreForm(
    val name: String,
    @param:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val dueDate: LocalDate,
    val scheduleType: String,
    val days: String? = null,
    val area: AreaDto,
)

data class ChoreUpdateForm(
    val name: String,
    @param:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val dueDate: LocalDate,
    val scheduleType: String,
    val days: String? = null,
    val area: AreaDto,
)

private fun parseSchedule(scheduleType: String, days: String?): Schedule {
    return when (scheduleType) {
        "one_time" -> Schedule.OneTime
        "every_n_days" -> {
            val daysInt = days?.toIntOrNull()
            require(daysInt != null && daysInt > 0) { "Days must be a positive number" }
            Schedule.EveryNDays(daysInt)
        }
        else -> error("Unknown schedule type: $scheduleType")
    }
}

fun ChoreForm.toSchedule(): Schedule = parseSchedule(scheduleType, days)

fun ChoreUpdateForm.toSchedule(): Schedule = parseSchedule(scheduleType, days)
