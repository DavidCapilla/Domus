package com.domus.habits.application

import com.domus.habits.core.HabitNotFoundException
import com.domus.habits.core.HabitRepository
import org.springframework.stereotype.Service

@Service
class DeleteHabitUseCase(
    private val habitRepository: HabitRepository,
) {
    fun deleteHabit(name: String) {
        if (habitRepository.findByName(name) == null) throw HabitNotFoundException(name)
        habitRepository.delete(name)
    }
}
