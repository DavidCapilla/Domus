package com.domus.habits.core

import java.time.LocalDate
import java.util.UUID

data class HabitLog(
    val id: UUID = UUID.randomUUID(),
    val habitId: UUID,
    val completedAt: LocalDate,
)
