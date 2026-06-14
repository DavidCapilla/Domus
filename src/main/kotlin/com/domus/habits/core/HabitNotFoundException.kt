package com.domus.habits.core

class HabitNotFoundException(name: String) : RuntimeException("Habit '$name' not found")
