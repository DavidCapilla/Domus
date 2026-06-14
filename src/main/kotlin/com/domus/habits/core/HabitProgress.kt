package com.domus.habits.core

data class HabitProgress(
    val habit: Habit,
    val currentCount: Int,
) {
    val targetCount: Int get() = habit.targetCount
    val percentage: Double get() = if (targetCount > 0) currentCount.toDouble() / targetCount else 0.0
}
