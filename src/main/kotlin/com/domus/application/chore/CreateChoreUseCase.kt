package com.domus.application.chore

import com.domus.core.chore.Chore
import com.domus.core.chore.ChoreAlreadyExistsException
import com.domus.core.chore.ChoreRepository
import org.springframework.stereotype.Service

@Service
class CreateChoreUseCase(val choreRepository: ChoreRepository) {

    fun addChore(chore: Chore) {
        if (!choreRepository.save(chore)) {
            throw ChoreAlreadyExistsException(chore.name)
        }
    }
}