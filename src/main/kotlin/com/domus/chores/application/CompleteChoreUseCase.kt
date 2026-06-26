package com.domus.chores.application

import com.domus.chores.core.ChoreNotFoundException
import com.domus.chores.core.ChoreRepository
import com.domus.chores.core.CompletionOutcome
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.UUID

@Service
class CompleteChoreUseCase(val choreRepository: ChoreRepository) {

    companion object {
        private val log = LoggerFactory.getLogger(CompleteChoreUseCase::class.java)
    }

    fun completeChore(id: UUID): CompletionOutcome {
        val chore = choreRepository.findById(id)
        if (chore == null) {
            log.warn("Chore not found for completion [id={}]", id)
            throw ChoreNotFoundException(id)
        }
        val outcome = chore.complete(LocalDate.now())
        when (outcome) {
            is CompletionOutcome.Finished -> {
                choreRepository.delete(id)
                log.info("Chore completed and removed [id={}]", id)
            }
            is CompletionOutcome.Continued -> {
                choreRepository.update(outcome.chore)
                log.info("Chore completed and rescheduled [id={}, nextDueDate={}]", id, outcome.chore.dueDate)
            }
        }
        return outcome
    }
}
