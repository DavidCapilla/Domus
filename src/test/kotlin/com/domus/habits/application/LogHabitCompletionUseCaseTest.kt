package com.domus.habits.application

import com.domus.habits.core.HabitLogRepository
import com.domus.habits.core.HabitNotFoundException
import com.domus.habits.core.HabitRepository
import com.domus.habits.core.TimeWindow
import com.domus.habits.createHabit
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class LogHabitCompletionUseCaseTest {

    private val habitRepository: HabitRepository = fakeHabitRepository()
    private val habitLogRepository: HabitLogRepository = fakeHabitLogRepository()
    private val clock = Clock.fixed(Instant.parse("2026-06-10T10:00:00Z"), ZoneId.of("UTC"))
    private lateinit var useCase: LogHabitCompletionUseCase

    @BeforeEach
    fun setUp() {
        useCase = LogHabitCompletionUseCase(habitRepository, habitLogRepository, clock)
        habitRepository.findAll().forEach { habitRepository.delete(it.name) }
    }

    @Test
    fun `logCompletion creates a log for existing habit`() {
        habitRepository.save(createHabit(name = "Read", timeWindow = TimeWindow.Daily))
        val log = useCase.logCompletion("Read")
        assertEquals(LocalDate.now(clock), log.completedAt)
        val logs = habitLogRepository.findByHabitId(log.habitId)
        assertEquals(1, logs.size)
    }

    @Test
    fun `logCompletion throws when habit not found`() {
        assertThrows<HabitNotFoundException> { useCase.logCompletion("Nonexistent") }
    }
}
