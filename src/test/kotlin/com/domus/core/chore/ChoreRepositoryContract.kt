package com.domus.core.chore

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.UUID

abstract class ChoreRepositoryContract {

    abstract val repository: ChoreRepository

    @Test
    fun `findAll returns empty list when no chores exist`() {
        assertTrue(repository.findAll().isEmpty())
    }

    @Test
    fun `findAll returns all saved chores`() {
        assertTrue(repository.save(Chore(UUID.randomUUID(), "Clean kitchen", LocalDate.now(), Schedule.OneTime)))
        assertTrue(repository.save(Chore(UUID.randomUUID(), "Do laundry", LocalDate.now(), Schedule.OneTime)))

        val result = repository.findAll()

        assertEquals(2, result.size)
        assertTrue(result.any { it.name == "Clean kitchen" })
        assertTrue(result.any { it.name == "Do laundry" })
    }

    @Test
    fun `duplicated chore by name is rejected even with different fields`() {
        assertTrue(repository.save(Chore(UUID.randomUUID(), "Clean kitchen", LocalDate.parse("2026-01-01"), Schedule.OneTime)))
        assertFalse(repository.save(Chore(UUID.randomUUID(), "Clean kitchen", LocalDate.parse("2026-06-06"), Schedule.OneTime)))

        assertEquals(1, repository.findAll().size)
    }

    @Test
    fun `duplicated chores are not saved`() {
        val chore = Chore(UUID.randomUUID(), "Clean kitchen", LocalDate.now(), Schedule.OneTime)
        assertTrue(repository.save(chore))
        assertFalse(repository.save(chore))

        assertEquals(1, repository.findAll().size)
    }

    @Test
    fun `delete removes existing chore`() {
        repository.save(Chore(UUID.randomUUID(), "Clean kitchen", LocalDate.now(), Schedule.OneTime))

        assertTrue(repository.delete("Clean kitchen"))
        assertTrue(repository.findAll().isEmpty())
    }

    @Test
    fun `delete returns false for non-existent chore`() {
        assertFalse(repository.delete("Non-existent"))
    }

    @Test
    fun `delete removes only the specified chore`() {
        repository.save(Chore(UUID.randomUUID(), "Clean kitchen", LocalDate.now(), Schedule.OneTime))
        repository.save(Chore(UUID.randomUUID(), "Do laundry", LocalDate.now(), Schedule.OneTime))

        assertTrue(repository.delete("Clean kitchen"))

        val result = repository.findAll()
        assertEquals(1, result.size)
        assertEquals("Do laundry", result.first().name)
    }
}
