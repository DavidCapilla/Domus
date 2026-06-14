package com.domus.habits.application

import com.domus.habits.core.HabitAlreadyExistsException
import com.domus.habits.core.HabitNotFoundException
import com.domus.habits.core.HabitRepository
import com.domus.habits.core.TimeWindow
import com.domus.habits.createHabit
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UpdateHabitUseCaseTest {

    private val habitRepository: HabitRepository = fakeHabitRepository()
    private lateinit var useCase: UpdateHabitUseCase

    @BeforeEach
    fun setUp() {
        useCase = UpdateHabitUseCase(habitRepository)
        habitRepository.findAll().forEach { habitRepository.delete(it.name) }
    }

    @Test
    fun `updateHabit modifies existing habit`() {
        habitRepository.save(createHabit(name = "Read"))
        val updated = useCase.updateHabit(
            currentName = "Read",
            newName = "Reading",
            description = "30 min",
            targetCount = 5,
            timeWindow = TimeWindow.Monthly,
        )
        assertEquals("Reading", updated.name)
        assertEquals("30 min", updated.description)
        assertEquals(5, updated.targetCount)
        assertEquals(TimeWindow.Monthly, updated.timeWindow)
    }

    @Test
    fun `updateHabit throws when original not found`() {
        assertThrows<HabitNotFoundException> {
            useCase.updateHabit("Nonexistent", "New", null, 1, TimeWindow.Daily)
        }
    }

    @Test
    fun `updateHabit throws when new name conflicts`() {
        habitRepository.save(createHabit(name = "Read"))
        habitRepository.save(createHabit(name = "Run"))
        assertThrows<HabitAlreadyExistsException> {
            useCase.updateHabit("Read", "Run", null, 1, TimeWindow.Daily)
        }
    }
}
