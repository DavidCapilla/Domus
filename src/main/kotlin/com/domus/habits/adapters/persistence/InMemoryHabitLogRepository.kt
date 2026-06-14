package com.domus.habits.adapters.persistence

import com.domus.habits.core.HabitLog
import com.domus.habits.core.HabitLogRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
class InMemoryHabitLogRepository : HabitLogRepository {

    private val logs: MutableList<HabitLog> = ArrayList()

    override fun findByHabitId(habitId: UUID): List<HabitLog> =
        logs.filter { it.habitId == habitId }

    override fun findByHabitIdAndDateBetween(habitId: UUID, start: LocalDate, end: LocalDate): List<HabitLog> =
        logs.filter { it.habitId == habitId && it.completedAt in start..end }

    override fun save(log: HabitLog): HabitLog {
        logs.add(log)
        return log
    }
}
