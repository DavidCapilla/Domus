package com.domus.chores.application

import com.domus.chores.core.Chore
import com.domus.chores.core.ChoreAlreadyExistsException
import com.domus.chores.core.ChoreRepository
import com.domus.chores.core.Schedule
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.UUID

@Service
class CreateChoreUseCase(val choreRepository: ChoreRepository) {

    fun addChore(name: String, dueDate: LocalDate, schedule: Schedule) {
        val chore =
            Chore(id = UUID.randomUUID(), name = name, dueDate = dueDate, schedule = schedule)
        if (!choreRepository.save(chore)) {
            throw ChoreAlreadyExistsException(name)
        }
    }
}