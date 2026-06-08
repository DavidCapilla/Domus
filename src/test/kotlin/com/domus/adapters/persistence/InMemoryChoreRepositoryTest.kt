package com.domus.adapters.persistence

import com.domus.createChore
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate

class InMemoryChoreRepositoryTest {

    private val repository = InMemoryChoreRepository()

    @Test
    fun `findAll returns empty list when no chores exist`() {
        val result = repository.findAll()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `findAll returns all saved chores`() {
        assertTrue(repository.save(createChore(name = "Clean kitchen")))
        assertTrue(repository.save(createChore(name = "Do laundry")))

        val result = repository.findAll()

        assertEquals(2, result.size)
        assertTrue(result.any { it.name == "Clean kitchen" })
        assertTrue(result.any { it.name == "Do laundry" })
    }

    @Test
    fun `duplicated chores are not saved`() {
        val chore = createChore(name = "Clean kitchen")
        assertTrue(repository.save(chore))
        assertFalse(repository.save(chore))

        val result = repository.findAll()

        assertEquals(1, result.size)
        assertTrue(result.any { it.name == "Clean kitchen" })
    }

    @Test
    fun `duplicated chore by name is rejected even with different fields`() {
        assertTrue(repository.save(createChore(name = "Clean kitchen", dueDate = LocalDate.parse("2026-01-01"))))
        assertFalse(repository.save(createChore(name = "Clean kitchen", dueDate = LocalDate.parse("2026-06-06"))))

        assertEquals(1, repository.findAll().size)
    }

    @Test
    fun `delete removes existing chore`() {
        repository.save(createChore(name = "Clean kitchen"))

        assertTrue(repository.delete("Clean kitchen"))
        assertTrue(repository.findAll().isEmpty())
    }

    @Test
    fun `delete returns false for non-existent chore`() {
        assertFalse(repository.delete("Non-existent"))
    }

    @Test
    fun `delete removes only the specified chore`() {
        repository.save(createChore(name = "Clean kitchen"))
        repository.save(createChore(name = "Do laundry"))

        assertTrue(repository.delete("Clean kitchen"))

        val result = repository.findAll()
        assertEquals(1, result.size)
        assertEquals("Do laundry", result.first().name)
    }
}
