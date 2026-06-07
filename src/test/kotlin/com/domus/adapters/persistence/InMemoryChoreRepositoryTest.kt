package com.domus.adapters.persistence

import com.domus.core.chore.Chore
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class InMemoryChoreRepositoryTest {

    private val repository = InMemoryChoreRepository()

    @Test
    fun `findAll returns empty list when no chores exist`() {
        val result = repository.findAll()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `findAll returns all saved chores`() {
        assertTrue(repository.save(Chore(name = "Clean kitchen")))
        assertTrue(repository.save(Chore(name = "Do laundry")))

        val result = repository.findAll()

        assertEquals(2, result.size)
        assertTrue(result.any { it.name == "Clean kitchen" })
        assertTrue(result.any { it.name == "Do laundry" })
    }

    @Test
    fun `duplicated chores are not saved`() {
        val chore = Chore(name = "Clean kitchen")
        assertTrue(repository.save(chore))
        assertFalse(repository.save(chore))

        val result = repository.findAll()

        assertEquals(1, result.size)
        assertTrue(result.any { it.name == "Clean kitchen" })
    }

    @Test
    fun `delete removes existing chore`() {
        val chore = Chore(name = "Clean kitchen")
        repository.save(chore)

        assertTrue(repository.delete(chore))
        assertTrue(repository.findAll().isEmpty())
    }

    @Test
    fun `delete returns false for non-existent chore`() {
        assertFalse(repository.delete(Chore(name = "Non-existent")))
    }

    @Test
    fun `delete removes only the specified chore`() {
        repository.save(Chore(name = "Clean kitchen"))
        repository.save(Chore(name = "Do laundry"))

        assertTrue(repository.delete(Chore(name = "Clean kitchen")))

        val result = repository.findAll()
        assertEquals(1, result.size)
        assertEquals("Do laundry", result.first().name)
    }
}
