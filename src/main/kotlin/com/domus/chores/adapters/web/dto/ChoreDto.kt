package com.domus.chores.adapters.web.dto

import com.domus.chores.core.Chore
import com.domus.chores.core.Schedule
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.time.LocalDate

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes(
    JsonSubTypes.Type(value = ScheduleDto.OneTime::class),
    JsonSubTypes.Type(value = ScheduleDto.EveryNDays::class),
)
sealed interface ScheduleDto {
    data object OneTime : ScheduleDto
    data class EveryNDays(val days: Int) : ScheduleDto
}

fun ScheduleDto.toDomain() = when (this) {
    is ScheduleDto.OneTime -> Schedule.OneTime
    is ScheduleDto.EveryNDays -> Schedule.EveryNDays(days)
}

fun Schedule.toDto() = when (this) {
    is Schedule.OneTime -> ScheduleDto.OneTime
    is Schedule.EveryNDays -> ScheduleDto.EveryNDays(days)
}

data class ChoreRequest(
    val name: String,
    val dueDate: LocalDate,
    val schedule: ScheduleDto = ScheduleDto.OneTime,
)

data class ChoreResponse(
    val name: String,
    val dueDate: LocalDate,
    val schedule: ScheduleDto?,
) {
    companion object {
        fun fromDomain(chore: Chore) = ChoreResponse(
            name = chore.name,
            dueDate = chore.dueDate,
            schedule = chore.schedule.toDto(),
        )
    }
}
