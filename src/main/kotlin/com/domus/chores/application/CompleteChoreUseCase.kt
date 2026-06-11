package com.domus.chores.application

import com.domus.chores.core.ChoreNotFoundException
import com.domus.chores.core.ChoreRepository
import com.domus.chores.core.CompletionOutcome
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class CompleteChoreUseCase(val choreRepository: ChoreRepository) {

    fun completeChore(name: String): CompletionOutcome {
        val chore = choreRepository.findByName(name) ?: throw ChoreNotFoundException(name)
        val outcome = chore.complete(LocalDate.now())
        when (outcome) {
            is CompletionOutcome.Finished -> choreRepository.delete(name)
            is CompletionOutcome.Continued -> choreRepository.update(name, outcome.chore)
        }
        return outcome
    }
}
