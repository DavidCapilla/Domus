package com.domus.habits.adapters.persistence

import com.domus.habits.core.HabitRepositoryContract

class InMemoryHabitRepositoryTest : HabitRepositoryContract() {
    override fun createRepository() = InMemoryHabitRepository()
}
