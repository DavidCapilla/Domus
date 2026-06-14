package com.domus.habits.application

import com.domus.habits.core.Habit
import com.domus.habits.core.HabitRepository
import org.springframework.stereotype.Service

@Service
class ListHabitsUseCase(
    private val habitRepository: HabitRepository,
) {
    fun getHabits(): List<Habit> = habitRepository.findAll()
}
