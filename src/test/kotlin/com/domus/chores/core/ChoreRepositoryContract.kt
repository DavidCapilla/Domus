package com.domus.chores.core

import com.domus.chores.createChore
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
        assertTrue(repository.save(createChore(name = "Clean kitchen", dueDate = LocalDate.now().plusDays(2))))
        assertTrue(repository.save(createChore(name = "Do laundry", dueDate = LocalDate.now().plusDays(5))))

        val result = repository.findAll()

        assertEquals(2, result.size)
        assertTrue(result.any { it.name == ChoreName.of("Clean kitchen") })
        assertTrue(result.any { it.name == ChoreName.of("Do laundry") })
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
        val found = repository.findByName(ChoreName.of("Clean kitchen"))
        assertTrue(found != null)
        assertEquals("Clean kitchen", found!!.name.value)
    }

    @Test
    fun `findByName returns null for non-existent chore`() {
        assertTrue(repository.findByName(ChoreName.of("Non-existent")) == null)
    }

    @Test
    fun `findById returns chore when it exists`() {
        val saved = createChore(name = "Clean kitchen")
        repository.save(saved)
        val found = repository.findById(saved.id)
        assertTrue(found != null)
        assertEquals("Clean kitchen", found!!.name.value)
    }

    @Test
    fun `findById returns null for non-existent id`() {
        assertTrue(repository.findById(UUID.randomUUID()) == null)
    }

    @Test
    fun `update changes chore fields`() {
        val saved = createChore(name = "Clean kitchen", dueDate = LocalDate.parse("2026-01-01"))
        repository.save(saved)
        assertTrue(repository.update(saved.id, createChore(id = saved.id, name = "Clean kitchen (updated)", dueDate = LocalDate.parse("2026-06-06"))))

        assertTrue(repository.findById(saved.id) != null)
        val updated = repository.findById(saved.id)
        assertTrue(updated != null)
        assertEquals(LocalDate.parse("2026-06-06"), updated!!.dueDate)
        assertEquals("Clean kitchen (updated)", updated.name.value)
    }

    @Test
    fun `update returns false for non-existent chore`() {
        assertFalse(repository.update(UUID.randomUUID(), createChore(name = "Anything")))
    }

    @Test
    fun `delete removes existing chore`() {
        val saved = createChore(name = "Clean kitchen", dueDate = LocalDate.now().plusWeeks(1))
        repository.save(saved)

        assertTrue(repository.delete(saved.id))
        assertTrue(repository.findAll().isEmpty())
    }

    @Test
    fun `delete returns false for non-existent chore`() {
        assertFalse(repository.delete(UUID.randomUUID()))
    }

    @Test
    fun `delete removes only the specified chore`() {
        val chore1 = createChore(name = "Clean kitchen", dueDate = LocalDate.now().plusDays(7))
        val chore2 = createChore(name = "Do laundry", dueDate = LocalDate.now().plusDays(14))
        repository.save(chore1)
        repository.save(chore2)

        assertTrue(repository.delete(chore1.id))

        val result = repository.findAll()
        assertEquals(1, result.size)
        assertEquals(ChoreName.of("Do laundry"), result.first().name)
    }
}
