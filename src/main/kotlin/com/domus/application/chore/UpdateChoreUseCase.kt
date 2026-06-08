package com.domus.application.chore

import com.domus.core.chore.Chore
import com.domus.core.chore.ChoreAlreadyExistsException
import com.domus.core.chore.ChoreNotFoundException
import com.domus.core.chore.ChoreRepository
import com.domus.core.chore.Schedule
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
