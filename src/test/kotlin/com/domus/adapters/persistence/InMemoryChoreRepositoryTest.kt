package com.domus.adapters.persistence

import com.domus.core.chore.Chore
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class InMemoryChoreRepositoryTest {

    @Test
    fun `findAll returns empty list when no chores exist`() {
        val repository = InMemoryChoreRepository(emptyMap())
        val result = repository.findAll()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `findAll returns all saved chores`() {
        val chore1 = Chore(name = "Clean kitchen")
        val chore2 = Chore(name = "Do laundry")
        val repository = InMemoryChoreRepository(
            mapOf(
                Pair(UUID.randomUUID(), chore1),
                Pair(UUID.randomUUID(), chore2)
            )
        )

        val result = repository.findAll()

        assertEquals(2, result.size)
        assertTrue(result.any { it.name == "Clean kitchen" })
        assertTrue(result.any { it.name == "Do laundry" })
    }
}
