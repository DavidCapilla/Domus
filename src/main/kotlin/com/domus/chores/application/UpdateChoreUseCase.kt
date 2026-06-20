package com.domus.chores.application

import com.domus.chores.core.Chore
import com.domus.chores.core.ChoreAlreadyExistsException
import com.domus.chores.core.ChoreNotFoundException
import com.domus.chores.core.ChoreRepository
import com.domus.chores.core.Schedule
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class UpdateChoreUseCase(val choreRepository: ChoreRepository) {

    companion object {
        private val log = LoggerFactory.getLogger(UpdateChoreUseCase::class.java)
    }

    fun updateChore(
        currentName: String,
        newName: String,
        dueDate: LocalDate,
        schedule: Schedule
    ): Chore {
        val existing = choreRepository.findByName(currentName)
        if (existing == null) {
            log.warn("Chore not found for update [name={}]", currentName)
            throw ChoreNotFoundException(currentName)
        }

        if (currentName != newName) {
            val existingWithNewName = choreRepository.findByName(newName)
            if (existingWithNewName != null) {
                log.warn("Chore not updated: name already exists [currentName={}, newName={}]", currentName, newName)
                throw ChoreAlreadyExistsException(newName)
            }
        }

        val updated = Chore(existing.id, newName, dueDate, schedule)
        choreRepository.update(currentName, updated)
        log.info("Chore updated [currentName={}, newName={}, dueDate={}, schedule={}]", currentName, newName, dueDate, schedule)
        return updated
    }
}
