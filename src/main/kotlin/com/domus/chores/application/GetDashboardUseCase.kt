package com.domus.chores.application

import com.domus.chores.core.Chore
import com.domus.chores.core.ChoreRepository
import com.domus.chores.core.Dashboard
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.LocalDate

@Service
open class GetDashboardUseCase(
    private val choreRepository: ChoreRepository,
    private val clock: Clock,
) {
    fun getDashboard(): Dashboard {
        val today = LocalDate.now(clock)
        val chores = choreRepository.findAll()
        val overdue = mutableListOf<Chore>()
        val dueToday = mutableListOf<Chore>()
        val upcoming = mutableListOf<Chore>()
        for (chore in chores) {
            when {
                chore.dueDate.isBefore(today) -> overdue.add(chore)
                chore.dueDate.isEqual(today) -> dueToday.add(chore)
                else -> upcoming.add(chore)
            }
        }
        return Dashboard(overdue, dueToday, upcoming)
    }
}
