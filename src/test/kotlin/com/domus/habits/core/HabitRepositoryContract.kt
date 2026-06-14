package com.domus.habits.core

import com.domus.habits.createHabit
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

abstract class HabitRepositoryContract {

    private lateinit var repository: HabitRepository

    abstract fun createRepository(): HabitRepository

    @BeforeEach
    fun setUp() {
        repository = createRepository()
    }

    @Test
    fun `findAll returns empty initially`() {
        assertTrue(repository.findAll().isEmpty())
    }

    @Test
    fun `save and findAll`() {
        repository.save(createHabit(name = "Read"))
        assertEquals(1, repository.findAll().size)
        assertEquals("Read", repository.findAll().first().name)
    }

    @Test
    fun `save multiple habits`() {
        repository.save(createHabit(name = "Read"))
        repository.save(createHabit(name = "Run"))
        assertEquals(2, repository.findAll().size)
    }

    @Test
    fun `save does not overwrite existing habit with same name`() {
        assertTrue(repository.save(createHabit(name = "Read")))
        assertFalse(repository.save(createHabit(name = "Read")))
        assertEquals(1, repository.findAll().size)
    }

    @Test
    fun `findByName returns habit when exists`() {
        repository.save(createHabit(name = "Read"))
        val habit = repository.findByName("Read")
        assertNotNull(habit)
        assertEquals("Read", habit!!.name)
    }

    @Test
    fun `findByName returns null when not exists`() {
        assertNull(repository.findByName("Nonexistent"))
    }

    @Test
    fun `findById returns habit when exists`() {
        val habit = createHabit(name = "Read")
        repository.save(habit)
        val found = repository.findById(habit.id)
        assertNotNull(found)
        assertEquals(habit.id, found!!.id)
    }

    @Test
    fun `findById returns null when not found`() {
        assertNull(repository.findById(UUID.randomUUID()))
    }

    @Test
    fun `update overwrites existing habit`() {
        repository.save(createHabit(name = "Read"))
        val updated = createHabit(name = "Read")
        assertTrue(repository.update("Read", updated))
        assertEquals("Read", repository.findAll().first().name)
    }

    @Test
    fun `update returns false for non-existing habit`() {
        assertFalse(repository.update("Nonexistent", createHabit(name = "Read")))
    }

    @Test
    fun `update renames habit`() {
        repository.save(createHabit(name = "Read"))
        val renamed = createHabit(name = "Reading")
        assertTrue(repository.update("Read", renamed))
        assertNull(repository.findByName("Read"))
        assertNotNull(repository.findByName("Reading"))
    }

    @Test
    fun `delete removes existing habit`() {
        repository.save(createHabit(name = "Read"))
        assertTrue(repository.delete("Read"))
        assertTrue(repository.findAll().isEmpty())
    }

    @Test
    fun `delete returns false for non-existing habit`() {
        assertFalse(repository.delete("Nonexistent"))
    }
}
