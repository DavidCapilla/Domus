package com.domus.habits.application

import com.domus.habits.core.HabitAlreadyExistsException
import com.domus.habits.core.HabitRepository
import com.domus.habits.core.TimeWindow
import com.domus.habits.createHabit
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CreateHabitUseCaseTest {

    private val habitRepository: HabitRepository = fakeHabitRepository()
    private lateinit var useCase: CreateHabitUseCase

    @BeforeEach
    fun setUp() {
        useCase = CreateHabitUseCase(habitRepository)
        habitRepository.findAll().forEach { habitRepository.delete(it.name) }
    }

    @Test
    fun `addHabit creates and saves habit`() {
        val habit = useCase.addHabit(name = "Read", description = "30 min", targetCount = 3, timeWindow = TimeWindow.Weekly)
        assertEquals("Read", habit.name)
        assertEquals("30 min", habit.description)
        assertEquals(3, habit.targetCount)
        assertEquals(TimeWindow.Weekly, habit.timeWindow)
        assertEquals(1, habitRepository.findAll().size)
    }

    @Test
    fun `addHabit throws when name already exists`() {
        habitRepository.save(createHabit(name = "Read"))
        assertThrows<HabitAlreadyExistsException> {
            useCase.addHabit(name = "Read", description = null, targetCount = 1, timeWindow = TimeWindow.Daily)
        }
    }
}
