package com.domus.chores.application

import com.domus.chores.core.ChoreNotFoundException
import com.domus.chores.core.ChoreRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DeleteChoreUseCase(val choreRepository: ChoreRepository) {

    companion object {
        private val log = LoggerFactory.getLogger(DeleteChoreUseCase::class.java)
    }

    fun deleteChore(id: UUID) {
        if (!choreRepository.delete(id)) {
            log.warn("Chore not found for deletion [id={}]", id)
            throw ChoreNotFoundException(id)
        }
        log.info("Chore deleted [id={}]", id)
    }
}