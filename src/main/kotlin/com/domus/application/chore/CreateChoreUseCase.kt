package com.domus.application.chore

import com.domus.core.chore.Chore
import com.domus.core.chore.ChoreAlreadyExistsException
import com.domus.core.chore.ChoreRepository
import com.domus.core.chore.Schedule
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