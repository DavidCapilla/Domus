package com.domus.habits.core

import java.time.LocalDate
import java.util.UUID

interface HabitLogRepository {
    fun findByHabitId(habitId: UUID): List<HabitLog>
    fun findByHabitIdAndDateBetween(habitId: UUID, start: LocalDate, end: LocalDate): List<HabitLog>
    fun save(log: HabitLog): HabitLog
}
