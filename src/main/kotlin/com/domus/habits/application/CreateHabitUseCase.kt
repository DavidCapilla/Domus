package com.domus.habits.application

import com.domus.habits.core.HabitAlreadyExistsException
import com.domus.habits.core.Habit
import com.domus.habits.core.HabitRepository
import com.domus.habits.core.TimeWindow
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.UUID

@Service
class CreateHabitUseCase(
    private val habitRepository: HabitRepository,
) {
    fun addHabit(
        name: String,
        description: String?,
        targetCount: Int,
        timeWindow: TimeWindow,
    ): Habit {
        if (habitRepository.findByName(name) != null) throw HabitAlreadyExistsException(name)
        val habit = Habit(
            id = UUID.randomUUID(),
            name = name,
            description = description,
            targetCount = targetCount,
            timeWindow = timeWindow,
        )
        habitRepository.save(habit)
        return habit
    }
}
