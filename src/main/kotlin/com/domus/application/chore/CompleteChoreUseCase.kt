package com.domus.application.chore

import com.domus.core.chore.ChoreNotFoundException
import com.domus.core.chore.ChoreRepository
import com.domus.core.chore.CompletionOutcome
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
