package com.domus.chores.application

import com.domus.chores.core.Chore
import com.domus.chores.core.ChoreAlreadyExistsException
import com.domus.chores.core.ChoreNotFoundException
import com.domus.chores.core.ChoreRepository
import com.domus.chores.core.Schedule
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class UpdateChoreUseCase(val choreRepository: ChoreRepository) {

    fun updateChore(
        currentName: String,
        newName: String,
        dueDate: LocalDate,
        schedule: Schedule
    ): Chore {
        val existing =
            choreRepository.findByName(currentName) ?: throw ChoreNotFoundException(currentName)

        if (currentName != newName) {
            val existingWithNewName = choreRepository.findByName(newName)
            if (existingWithNewName != null) {
                throw ChoreAlreadyExistsException(newName)
            }
        }

        val updated = Chore(existing.id, newName, dueDate, schedule)
        choreRepository.update(currentName, updated)
        return updated
    }
}
