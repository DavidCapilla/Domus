package com.domus.chores.application

import com.domus.chores.core.ChoreNotFoundException
import com.domus.chores.core.ChoreRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class DeleteChoreUseCase(val choreRepository: ChoreRepository) {

    companion object {
        private val log = LoggerFactory.getLogger(DeleteChoreUseCase::class.java)
    }

    fun deleteChore(choreName: String) {
        if (!choreRepository.delete(choreName)) {
            log.warn("Chore not found for deletion [name={}]", choreName)
            throw ChoreNotFoundException(choreName)
        }
        log.info("Chore deleted [name={}]", choreName)
    }
}