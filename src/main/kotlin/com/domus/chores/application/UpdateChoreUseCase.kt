package com.domus.chores.application

import com.domus.chores.core.Area
import com.domus.chores.core.Chore
import com.domus.chores.core.ChoreAlreadyExistsException
import com.domus.chores.core.ChoreName
import com.domus.chores.core.ChoreNotFoundException
import com.domus.chores.core.ChoreRepository
import com.domus.chores.core.Schedule
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.UUID

@Service
class UpdateChoreUseCase(val choreRepository: ChoreRepository) {

    companion object {
        private val log = LoggerFactory.getLogger(UpdateChoreUseCase::class.java)
    }

    fun updateChore(
        id: UUID,
        name: String,
        dueDate: LocalDate,
        schedule: Schedule,
        area: Area,
    ): Chore {
        val choreName = ChoreName.of(name)
        val existing = choreRepository.findById(id)
        if (existing == null) {
            log.warn("Chore not found for update [id={}]", id)
            throw ChoreNotFoundException(id)
        }

        if (existing.name != choreName && choreRepository.findByName(choreName) != null) {
            log.warn("Chore not updated: name already exists [id={}, name={}]", id, name)
            throw ChoreAlreadyExistsException(name)
        }

        val updated = Chore(existing.id, choreName, dueDate, schedule, area)
        choreRepository.update(updated)
        log.info(
            "Chore updated [id={}, newName={}, dueDate={}, schedule={}, area={}]",
            id,
            name,
            dueDate,
            schedule,
            area,
        )
        return updated
    }
}
