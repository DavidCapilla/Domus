package com.domus.application.chore

import com.domus.core.chore.Chore
import com.domus.core.chore.ChoreNotFoundException
import com.domus.core.chore.ChoreRepository
import org.springframework.stereotype.Service

@Service
class DeleteChoreUseCase(val choreRepository: ChoreRepository) {

    fun deleteChore(choreName: String) {
        val chore = choreRepository.findByName(choreName) ?: throw ChoreNotFoundException(choreName)
        if (!choreRepository.delete(chore)) {
            throw ChoreNotFoundException(choreName)
        }
    }
}