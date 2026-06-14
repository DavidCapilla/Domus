package com.domus.habits.adapters.persistence

import com.domus.habits.core.Habit
import com.domus.habits.core.HabitRepository
import org.springframework.stereotype.Repository
import java.util.HashSet
import java.util.UUID

@Repository
class InMemoryHabitRepository : HabitRepository {

    private val habits: MutableSet<Habit> = HashSet()

    override fun findAll(): List<Habit> = habits.toList()

    override fun findByName(name: String): Habit? = habits.find { it.name == name }

    override fun findById(id: UUID): Habit? = habits.find { it.id == id }

    override fun save(habit: Habit): Boolean {
        if (habits.any { it.name == habit.name }) return false
        return habits.add(habit)
    }

    override fun update(currentName: String, habit: Habit): Boolean {
        val existing = habits.find { it.name == currentName } ?: return false
        habits.remove(existing)
        habits.add(habit)
        return true
    }

    override fun delete(habitName: String): Boolean = habits.removeIf { it.name == habitName }
}
