package com.domus.habits.adapters.persistence

import com.domus.habits.core.HabitLog
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.UUID

class InMemoryHabitLogRepositoryTest {

    private lateinit var repository: InMemoryHabitLogRepository
    private val habitId = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        repository = InMemoryHabitLogRepository()
    }

    @Test
    fun `findByHabitId returns empty initially`() {
        assertTrue(repository.findByHabitId(habitId).isEmpty())
    }

    @Test
    fun `save and findByHabitId`() {
        val log = HabitLog(habitId = habitId, completedAt = LocalDate.now())
        repository.save(log)
        val logs = repository.findByHabitId(habitId)
        assertEquals(1, logs.size)
        assertEquals(log.id, logs.first().id)
    }

    @Test
    fun `findByHabitId ignores other habits`() {
        repository.save(HabitLog(habitId = habitId, completedAt = LocalDate.now()))
        repository.save(HabitLog(habitId = UUID.randomUUID(), completedAt = LocalDate.now()))
        assertEquals(1, repository.findByHabitId(habitId).size)
    }

    @Test
    fun `findByHabitIdAndDateBetween filters by date range`() {
        repository.save(HabitLog(habitId = habitId, completedAt = LocalDate.of(2026, 6, 1)))
        repository.save(HabitLog(habitId = habitId, completedAt = LocalDate.of(2026, 6, 10)))
        repository.save(HabitLog(habitId = habitId, completedAt = LocalDate.of(2026, 6, 20)))

        val result = repository.findByHabitIdAndDateBetween(
            habitId,
            LocalDate.of(2026, 6, 5),
            LocalDate.of(2026, 6, 15),
        )
        assertEquals(1, result.size)
        assertEquals(LocalDate.of(2026, 6, 10), result.first().completedAt)
    }
}
