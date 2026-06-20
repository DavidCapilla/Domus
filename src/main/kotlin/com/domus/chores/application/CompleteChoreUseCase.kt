package com.domus.chores.application

import com.domus.chores.core.ChoreNotFoundException
import com.domus.chores.core.ChoreRepository
import com.domus.chores.core.CompletionOutcome
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class CompleteChoreUseCase(val choreRepository: ChoreRepository) {

    companion object {
        private val log = LoggerFactory.getLogger(CompleteChoreUseCase::class.java)
    }

    fun completeChore(name: String): CompletionOutcome {
        val chore = choreRepository.findByName(name)
        if (chore == null) {
            log.warn("Chore not found for completion [name={}]", name)
            throw ChoreNotFoundException(name)
        }
        val outcome = chore.complete(LocalDate.now())
        when (outcome) {
            is CompletionOutcome.Finished -> {
                choreRepository.delete(name)
                log.info("Chore completed and removed [name={}]", name)
            }
            is CompletionOutcome.Continued -> {
                choreRepository.update(name, outcome.chore)
                log.info("Chore completed and rescheduled [name={}, nextDueDate={}]", name, outcome.chore.dueDate)
            }
        }
        return outcome
    }
}
