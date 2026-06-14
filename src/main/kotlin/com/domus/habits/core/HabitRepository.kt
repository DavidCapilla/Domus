package com.domus.habits.core

import java.util.UUID

interface HabitRepository {
    fun findAll(): List<Habit>
    fun findByName(name: String): Habit?
    fun findById(id: UUID): Habit?
    fun save(habit: Habit): Boolean
    fun update(currentName: String, habit: Habit): Boolean
    fun delete(habitName: String): Boolean
}
