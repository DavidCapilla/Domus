package com.domus.chores.adapters.web.dto

import com.domus.chores.core.Area
import com.domus.chores.core.Chore
import com.domus.chores.core.Schedule
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes(
    JsonSubTypes.Type(value = ScheduleDto.OneTime::class),
    JsonSubTypes.Type(value = ScheduleDto.EveryNDays::class),
)
sealed interface ScheduleDto {
    data object OneTime : ScheduleDto
    data class EveryNDays(val days: Int) : ScheduleDto

    @get:JsonIgnore
    val displayName: String
        get() = when (this) {
            is OneTime -> "one time chore"
            is EveryNDays -> "recurrent chore: every $days days"
        }

    @get:JsonIgnore
    val isOneTime: Boolean
        get() = this is OneTime
}

enum class AreaDto(val displayName: String) {
    KITCHEN("Kitchen"),
    BATHROOM("Bathroom"),
    EXTERIOR("Exterior"),
    LIVING_ROOM("Living room"),
    BEDROOM("Bedroom"),
    ENTIRE_HOUSE("Entire house"),
    NONE("No Area"),
}

fun ScheduleDto.toDomain() = when (this) {
    is ScheduleDto.OneTime -> Schedule.OneTime
    is ScheduleDto.EveryNDays -> Schedule.EveryNDays(days)
}

fun Schedule.toDto() = when (this) {
    is Schedule.OneTime -> ScheduleDto.OneTime
    is Schedule.EveryNDays -> ScheduleDto.EveryNDays(days)
}

fun AreaDto.toDomain() = when (this) {
    AreaDto.KITCHEN -> Area.KITCHEN
    AreaDto.BATHROOM -> Area.BATHROOM
    AreaDto.EXTERIOR -> Area.EXTERIOR
    AreaDto.LIVING_ROOM -> Area.LIVING_ROOM
    AreaDto.BEDROOM -> Area.BEDROOM
    AreaDto.ENTIRE_HOUSE -> Area.ENTIRE_HOUSE
    AreaDto.NONE -> Area.NONE
}

fun Area.toDto() = when (this) {
    Area.KITCHEN -> AreaDto.KITCHEN
    Area.BATHROOM -> AreaDto.BATHROOM
    Area.EXTERIOR -> AreaDto.EXTERIOR
    Area.LIVING_ROOM -> AreaDto.LIVING_ROOM
    Area.BEDROOM -> AreaDto.BEDROOM
    Area.ENTIRE_HOUSE -> AreaDto.ENTIRE_HOUSE
    Area.NONE -> AreaDto.NONE
}

data class ChoreRequest(
    @field:NotBlank
    val name: String,
    @field:NotNull
    val dueDate: LocalDate,
    val schedule: ScheduleDto = ScheduleDto.OneTime,
    val area: AreaDto = AreaDto.NONE,
)

data class ChoreResponse(
    val id: String,
    val name: String,
    val dueDate: LocalDate,
    val schedule: ScheduleDto,
    val area: AreaDto,
) {
    companion object {
        fun fromDomain(chore: Chore) = ChoreResponse(
            id = chore.id.toString(),
            name = chore.name.value,
            dueDate = chore.dueDate,
            schedule = chore.schedule.toDto(),
            area = chore.area.toDto(),
        )
    }
}
