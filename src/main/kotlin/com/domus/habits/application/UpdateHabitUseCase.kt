package com.domus.habits.application

import com.domus.habits.core.Habit
import com.domus.habits.core.HabitAlreadyExistsException
import com.domus.habits.core.HabitNotFoundException
import com.domus.habits.core.HabitRepository
import com.domus.habits.core.TimeWindow
import org.springframework.stereotype.Service

@Service
class UpdateHabitUseCase(
    private val habitRepository: HabitRepository,
) {
    fun updateHabit(
        currentName: String,
        newName: String,
        description: String?,
        targetCount: Int,
        timeWindow: TimeWindow,
    ): Habit {
        val existing = habitRepository.findByName(currentName)
            ?: throw HabitNotFoundException(currentName)

        if (currentName != newName && habitRepository.findByName(newName) != null) {
            throw HabitAlreadyExistsException(newName)
        }

        val updated = existing.copy(
            name = newName,
            description = description,
            targetCount = targetCount,
            timeWindow = timeWindow,
        )
        habitRepository.update(currentName, updated)
        return updated
    }
}
