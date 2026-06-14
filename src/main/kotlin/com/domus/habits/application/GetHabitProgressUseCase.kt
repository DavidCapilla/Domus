package com.domus.habits.application

import com.domus.habits.core.HabitLogRepository
import com.domus.habits.core.HabitProgress
import com.domus.habits.core.HabitRepository
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.LocalDate

@Service
class GetHabitProgressUseCase(
    private val habitRepository: HabitRepository,
    private val habitLogRepository: HabitLogRepository,
    private val clock: Clock,
) {
    fun getProgress(): List<HabitProgress> {
        val today = LocalDate.now(clock)
        return habitRepository.findAll().map { habit ->
            val (start, end) = habit.timeWindow.dateRange(today)
            val count = habitLogRepository.findByHabitIdAndDateBetween(habit.id, start, end).size
            HabitProgress(habit = habit, currentCount = count)
        }
    }
}
