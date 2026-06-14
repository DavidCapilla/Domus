package com.domus.habits

import com.domus.habits.core.Habit
import com.domus.habits.core.TimeWindow
import java.time.LocalDate
import java.util.UUID

fun createHabit(
    id: UUID = UUID.randomUUID(),
    name: String,
    description: String? = null,
    targetCount: Int = 1,
    timeWindow: TimeWindow = TimeWindow.Daily,
): Habit = Habit(id = id, name = name, description = description, targetCount = targetCount, timeWindow = timeWindow)
