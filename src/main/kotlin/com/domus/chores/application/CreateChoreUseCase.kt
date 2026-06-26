package com.domus.chores.application

import com.domus.chores.core.Chore
import com.domus.chores.core.ChoreAlreadyExistsException
import com.domus.chores.core.ChoreName
import com.domus.chores.core.ChoreRepository
import com.domus.chores.core.Schedule
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.UUID

@Service
class CreateChoreUseCase(val choreRepository: ChoreRepository) {

    companion object {
        private val log = LoggerFactory.getLogger(CreateChoreUseCase::class.java)
    }

    fun addChore(name: String, dueDate: LocalDate, schedule: Schedule) {
        if (choreRepository.findByName(ChoreName.of(name)) != null) {
            log.warn("Chore not created: name already exists [name={}]", name)
            throw ChoreAlreadyExistsException(name)
        }
        val chore = Chore(
            id = UUID.randomUUID(),
            name = ChoreName.of(name),
            dueDate = dueDate,
            schedule = schedule
        )
        if (!choreRepository.save(chore)) {
            log.warn(
                "Chore not created, try again [id={}, name={}, dueDate={}, schedule={}]",
                chore.id,
                chore.name.value,
                chore.dueDate,
                chore.schedule,
            )
            throw ChoreAlreadyExistsException(name)
        }
        log.info(
            "Chore created [id={}, name={}, dueDate={}, schedule={}]",
            chore.id,
            chore.name.value,
            chore.dueDate,
            chore.schedule,
        )
    }
}