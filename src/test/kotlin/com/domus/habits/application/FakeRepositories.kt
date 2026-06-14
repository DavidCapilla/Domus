package com.domus.habits.application

import com.domus.habits.core.Habit
import com.domus.habits.core.HabitLog
import com.domus.habits.core.HabitLogRepository
import com.domus.habits.core.HabitRepository
import java.time.LocalDate
import java.util.UUID

fun fakeHabitRepository(): HabitRepository = object : HabitRepository {
    private val habits: MutableSet<Habit> = HashSet()

    override fun findAll() = habits.toList()
    override fun findByName(name: String) = habits.find { it.name == name }
    override fun findById(id: UUID) = habits.find { it.id == id }
    override fun save(habit: Habit) = habits.add(habit)
    override fun update(name: String, habit: Habit): Boolean {
        val existing = habits.find { it.name == name } ?: return false
        habits.remove(existing)
        habits.add(habit)
        return true
    }
    override fun delete(name: String) = habits.removeIf { it.name == name }
}

fun fakeHabitLogRepository(): HabitLogRepository = object : HabitLogRepository {
    private val logs: MutableList<HabitLog> = ArrayList()

    override fun findByHabitId(habitId: UUID) = logs.filter { it.habitId == habitId }
    override fun findByHabitIdAndDateBetween(habitId: UUID, start: LocalDate, end: LocalDate) =
        logs.filter { it.habitId == habitId && it.completedAt in start..end }
    override fun save(log: HabitLog): HabitLog {
        logs.add(log)
        return log
    }
}
