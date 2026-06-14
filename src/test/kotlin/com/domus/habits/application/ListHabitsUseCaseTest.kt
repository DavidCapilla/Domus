package com.domus.habits.application

import com.domus.habits.core.HabitRepository
import com.domus.habits.createHabit
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ListHabitsUseCaseTest {

    private val habitRepository: HabitRepository = fakeHabitRepository()
    private lateinit var useCase: ListHabitsUseCase

    @BeforeEach
    fun setUp() {
        useCase = ListHabitsUseCase(habitRepository)
        habitRepository.findAll().forEach { habitRepository.delete(it.name) }
    }

    @Test
    fun `getHabits returns empty initially`() {
        assertTrue(useCase.getHabits().isEmpty())
    }

    @Test
    fun `getHabits returns saved habits`() {
        habitRepository.save(createHabit(name = "Read"))
        habitRepository.save(createHabit(name = "Run"))
        assertEquals(2, useCase.getHabits().size)
    }
}
