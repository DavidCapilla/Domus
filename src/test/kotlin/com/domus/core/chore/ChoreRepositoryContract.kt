package com.domus.core.chore

import com.domus.createChore
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate

abstract class ChoreRepositoryContract {

    abstract val repository: ChoreRepository

    @Test
    fun `findAll returns empty list when no chores exist`() {
        assertTrue(repository.findAll().isEmpty())
    }

    @Test
    fun `findAll returns all saved chores`() {
        assertTrue(repository.save(createChore(name = "Clean kitchen", dueDate = LocalDate.now().plusDays(2))))
        assertTrue(repository.save(createChore(name = "Do laundry", dueDate = LocalDate.now().plusDays(5))))

        val result = repository.findAll()

        assertEquals(2, result.size)
        assertTrue(result.any { it.name == "Clean kitchen" })
        assertTrue(result.any { it.name == "Do laundry" })
    }

    @Test
    fun `duplicated chore by name is rejected even with different fields`() {
        assertTrue(repository.save(createChore(name = "Clean kitchen", dueDate = LocalDate.parse("2026-01-01"))))
        assertFalse(repository.save(createChore(name = "Clean kitchen", dueDate = LocalDate.parse("2026-06-06"))))

        assertEquals(1, repository.findAll().size)
    }

    @Test
    fun `duplicated chores are not saved`() {
        val chore = createChore(name = "Clean kitchen", dueDate = LocalDate.now().plusDays(3))
        assertTrue(repository.save(chore))
        assertFalse(repository.save(chore))

        assertEquals(1, repository.findAll().size)
    }

    @Test
    fun `findByName returns chore when it exists`() {
        repository.save(createChore(name = "Clean kitchen"))
        val found = repository.findByName("Clean kitchen")
        assertTrue(found != null)
        assertEquals("Clean kitchen", found!!.name)
    }

    @Test
    fun `findByName returns null for non-existent chore`() {
        assertTrue(repository.findByName("Non-existent") == null)
    }

    @Test
    fun `update changes chore fields`() {
        repository.save(createChore(name = "Clean kitchen", dueDate = LocalDate.parse("2026-01-01")))
        assertTrue(repository.update("Clean kitchen", createChore(name = "Clean kitchen (updated)", dueDate = LocalDate.parse("2026-06-06"))))

        assertTrue(repository.findByName("Clean kitchen") == null)
        val updated = repository.findByName("Clean kitchen (updated)")
        assertTrue(updated != null)
        assertEquals(LocalDate.parse("2026-06-06"), updated!!.dueDate)
    }

    @Test
    fun `update returns false for non-existent chore`() {
        assertFalse(repository.update("Non-existent", createChore(name = "Anything")))
    }

    @Test
    fun `delete removes existing chore`() {
        repository.save(createChore(name = "Clean kitchen", dueDate = LocalDate.now().plusWeeks(1)))

        assertTrue(repository.delete("Clean kitchen"))
        assertTrue(repository.findAll().isEmpty())
    }

    @Test
    fun `delete returns false for non-existent chore`() {
        assertFalse(repository.delete("Non-existent"))
    }

    @Test
    fun `delete removes only the specified chore`() {
        repository.save(createChore(name = "Clean kitchen", dueDate = LocalDate.now().plusDays(7)))
        repository.save(createChore(name = "Do laundry", dueDate = LocalDate.now().plusDays(14)))

        assertTrue(repository.delete("Clean kitchen"))

        val result = repository.findAll()
        assertEquals(1, result.size)
        assertEquals("Do laundry", result.first().name)
    }
}
