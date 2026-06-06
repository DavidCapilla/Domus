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
}
