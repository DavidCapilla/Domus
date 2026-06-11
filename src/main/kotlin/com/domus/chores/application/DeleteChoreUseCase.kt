package com.domus.chores.application

import com.domus.chores.core.ChoreNotFoundException
import com.domus.chores.core.ChoreRepository
import org.springframework.stereotype.Service

@Service
class DeleteChoreUseCase(val choreRepository: ChoreRepository) {

    fun deleteChore(choreName: String) {
        if (!choreRepository.delete(choreName)) {
            throw ChoreNotFoundException(choreName)
        }
    }
}