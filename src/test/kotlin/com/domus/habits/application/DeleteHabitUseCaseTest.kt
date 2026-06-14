package com.domus.habits.application

import com.domus.habits.core.HabitNotFoundException
import com.domus.habits.core.HabitRepository
import com.domus.habits.createHabit
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DeleteHabitUseCaseTest {

    private val habitRepository: HabitRepository = fakeHabitRepository()
    private lateinit var useCase: DeleteHabitUseCase

    @BeforeEach
    fun setUp() {
        useCase = DeleteHabitUseCase(habitRepository)
        habitRepository.findAll().forEach { habitRepository.delete(it.name) }
    }

    @Test
    fun `deleteHabit removes habit`() {
        habitRepository.save(createHabit(name = "Read"))
        useCase.deleteHabit("Read")
        assertTrue(habitRepository.findAll().isEmpty())
    }

    @Test
    fun `deleteHabit throws when not found`() {
        assertThrows<HabitNotFoundException> { useCase.deleteHabit("Nonexistent") }
    }
}
