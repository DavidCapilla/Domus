package com.domus.application.chore

import com.domus.core.chore.ChoreNotFoundException
import com.domus.core.chore.ChoreRepository
import org.springframework.stereotype.Service

@Service
class DeleteChoreUseCase(val choreRepository: ChoreRepository) {

    fun deleteChore(choreName: String) {
        if (!choreRepository.delete(choreName)) {
            throw ChoreNotFoundException(choreName)
        }
    }
}