package com.domus.chores.application

import com.domus.chores.core.Chore
import com.domus.chores.core.ChoreRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GetChoreUseCase(val choreRepository: ChoreRepository) {

    fun getChore(id: UUID): Chore? {
        return choreRepository.findById(id)
    }
}
