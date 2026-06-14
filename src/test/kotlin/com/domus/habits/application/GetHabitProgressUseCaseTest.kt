package com.domus.habits.application

import com.domus.habits.core.HabitLogRepository
import com.domus.habits.core.HabitRepository
import com.domus.habits.core.TimeWindow
import com.domus.habits.createHabit
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class GetHabitProgressUseCaseTest {

    private val habitRepository: HabitRepository = fakeHabitRepository()
    private val habitLogRepository: HabitLogRepository = fakeHabitLogRepository()
    private val clock = Clock.fixed(Instant.parse("2026-06-10T10:00:00Z"), ZoneId.of("UTC"))
    private lateinit var useCase: GetHabitProgressUseCase

    @BeforeEach
    fun setUp() {
        useCase = GetHabitProgressUseCase(habitRepository, habitLogRepository, clock)
        habitRepository.findAll().forEach { habitRepository.delete(it.name) }
    }

    @Test
    fun `getProgress returns empty when no habits`() {
        assertEquals(0, useCase.getProgress().size)
    }

    @Test
    fun `getProgress returns zero count for habit with no logs`() {
        habitRepository.save(createHabit(name = "Read", targetCount = 3, timeWindow = TimeWindow.Daily))
        val progress = useCase.getProgress()
        assertEquals(1, progress.size)
        assertEquals(0, progress.first().currentCount)
        assertEquals(3, progress.first().targetCount)
        assertEquals(0.0, progress.first().percentage)
    }

    @Test
    fun `getProgress counts logs in daily window`() {
        val habit = createHabit(name = "Read", targetCount = 2, timeWindow = TimeWindow.Daily)
        habitRepository.save(habit)
        habitLogRepository.save(com.domus.habits.core.HabitLog(habitId = habit.id, completedAt = LocalDate.now(clock)))
        habitLogRepository.save(com.domus.habits.core.HabitLog(habitId = habit.id, completedAt = LocalDate.now(clock)))

        val progress = useCase.getProgress()
        assertEquals(2, progress.first().currentCount)
        assertEquals(1.0, progress.first().percentage)
    }

    @Test
    fun `getProgress ignores logs outside time window`() {
        val habit = createHabit(name = "Read", targetCount = 1, timeWindow = TimeWindow.Daily)
        habitRepository.save(habit)
        habitLogRepository.save(com.domus.habits.core.HabitLog(habitId = habit.id, completedAt = LocalDate.now(clock)))
        habitLogRepository.save(com.domus.habits.core.HabitLog(habitId = habit.id, completedAt = LocalDate.now(clock).minusDays(1)))

        val progress = useCase.getProgress()
        assertEquals(1, progress.first().currentCount)
    }

    @Test
    fun `getProgress counts weekly logs`() {
        val monday = LocalDate.of(2026, 6, 8)
        val habit = createHabit(name = "Read", targetCount = 3, timeWindow = TimeWindow.Weekly)
        habitRepository.save(habit)
        habitLogRepository.save(com.domus.habits.core.HabitLog(habitId = habit.id, completedAt = monday))
        habitLogRepository.save(com.domus.habits.core.HabitLog(habitId = habit.id, completedAt = monday.plusDays(1)))
        habitLogRepository.save(com.domus.habits.core.HabitLog(habitId = habit.id, completedAt = monday.plusDays(2)))

        val progress = useCase.getProgress()
        assertEquals(3, progress.first().currentCount)
    }

    @Test
    fun `getProgress counts monthly logs`() {
        val habit = createHabit(name = "Read", targetCount = 5, timeWindow = TimeWindow.Monthly)
        habitRepository.save(habit)
        habitLogRepository.save(com.domus.habits.core.HabitLog(habitId = habit.id, completedAt = LocalDate.of(2026, 6, 1)))
        habitLogRepository.save(com.domus.habits.core.HabitLog(habitId = habit.id, completedAt = LocalDate.of(2026, 6, 5)))
        habitLogRepository.save(com.domus.habits.core.HabitLog(habitId = habit.id, completedAt = LocalDate.of(2026, 6, 10)))

        val progress = useCase.getProgress()
        assertEquals(3, progress.first().currentCount)
    }

    @Test
    fun `getProgress counts logs for EveryNDays`() {
        val today = LocalDate.of(2026, 6, 10)
        val habit = createHabit(name = "Read", targetCount = 2, timeWindow = TimeWindow.EveryNDays(7))
        habitRepository.save(habit)
        habitLogRepository.save(com.domus.habits.core.HabitLog(habitId = habit.id, completedAt = today.minusDays(1)))
        habitLogRepository.save(com.domus.habits.core.HabitLog(habitId = habit.id, completedAt = today.minusDays(6)))
        habitLogRepository.save(com.domus.habits.core.HabitLog(habitId = habit.id, completedAt = today.minusDays(8)))

        val progress = useCase.getProgress()
        assertEquals(2, progress.first().currentCount)
    }

    @Test
    fun `getProgress handles multiple habits`() {
        val h1 = createHabit(name = "Read", targetCount = 1, timeWindow = TimeWindow.Daily)
        val h2 = createHabit(name = "Run", targetCount = 1, timeWindow = TimeWindow.Daily)
        habitRepository.save(h1)
        habitRepository.save(h2)
        habitLogRepository.save(com.domus.habits.core.HabitLog(habitId = h1.id, completedAt = LocalDate.now(clock)))

        val progress = useCase.getProgress()
        assertEquals(2, progress.size)
        assertEquals(1, progress.find { it.habit.name == "Read" }!!.currentCount)
        assertEquals(0, progress.find { it.habit.name == "Run" }!!.currentCount)
    }
}
