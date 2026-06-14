package com.domus.habits.core

class HabitAlreadyExistsException(name: String) : RuntimeException("Habit '$name' already exists")
