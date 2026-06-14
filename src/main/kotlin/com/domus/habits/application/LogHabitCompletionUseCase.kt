package com.domus.habits.application

import com.domus.habits.core.HabitLog
import com.domus.habits.core.HabitLogRepository
import com.domus.habits.core.HabitNotFoundException
import com.domus.habits.core.HabitRepository
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.LocalDate
import java.util.UUID

@Service
class LogHabitCompletionUseCase(
    private val habitRepository: HabitRepository,
    private val habitLogRepository: HabitLogRepository,
    private val clock: Clock,
) {
    fun logCompletion(habitName: String): HabitLog {
        val habit = habitRepository.findByName(habitName)
            ?: throw HabitNotFoundException(habitName)

        val log = HabitLog(
            id = UUID.randomUUID(),
            habitId = habit.id,
            completedAt = LocalDate.now(clock),
        )
        return habitLogRepository.save(log)
    }
}
