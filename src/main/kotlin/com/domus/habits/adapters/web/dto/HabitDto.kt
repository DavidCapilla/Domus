package com.domus.habits.adapters.web.dto

import com.domus.habits.core.Habit
import com.domus.habits.core.HabitProgress
import com.domus.habits.core.TimeWindow
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes(
    JsonSubTypes.Type(value = TimeWindowDto.Daily::class, name = "daily"),
    JsonSubTypes.Type(value = TimeWindowDto.Weekly::class, name = "weekly"),
    JsonSubTypes.Type(value = TimeWindowDto.Monthly::class, name = "monthly"),
    JsonSubTypes.Type(value = TimeWindowDto.EveryNDays::class, name = "every_n_days"),
)
sealed interface TimeWindowDto {
    val type: String
    data class Daily(override val type: String = "daily") : TimeWindowDto
    data class Weekly(override val type: String = "weekly") : TimeWindowDto
    data class Monthly(override val type: String = "monthly") : TimeWindowDto
    data class EveryNDays(val days: Int, override val type: String = "every_n_days") : TimeWindowDto
}

fun TimeWindowDto.toDomain() = when (this) {
    is TimeWindowDto.Daily -> TimeWindow.Daily
    is TimeWindowDto.Weekly -> TimeWindow.Weekly
    is TimeWindowDto.Monthly -> TimeWindow.Monthly
    is TimeWindowDto.EveryNDays -> TimeWindow.EveryNDays(days)
}

fun TimeWindow.toDto(): TimeWindowDto = when (this) {
    is TimeWindow.Daily -> TimeWindowDto.Daily()
    is TimeWindow.Weekly -> TimeWindowDto.Weekly()
    is TimeWindow.Monthly -> TimeWindowDto.Monthly()
    is TimeWindow.EveryNDays -> TimeWindowDto.EveryNDays(days)
}

data class HabitRequest(
    val name: String,
    val description: String?,
    val targetCount: Int,
    val timeWindow: TimeWindowDto = TimeWindowDto.Daily(),
)

data class HabitResponse(
    val name: String,
    val description: String?,
    val targetCount: Int,
    val timeWindow: TimeWindowDto?,
    val currentCount: Int = 0,
    val progress: Double = 0.0,
) {
    companion object {
        fun fromDomain(progress: HabitProgress) = HabitResponse(
            name = progress.habit.name,
            description = progress.habit.description,
            targetCount = progress.habit.targetCount,
            timeWindow = progress.habit.timeWindow.toDto(),
            currentCount = progress.currentCount,
            progress = progress.percentage,
        )

        fun fromDomain(habit: Habit) = HabitResponse(
            name = habit.name,
            description = habit.description,
            targetCount = habit.targetCount,
            timeWindow = habit.timeWindow.toDto(),
        )
    }
}
