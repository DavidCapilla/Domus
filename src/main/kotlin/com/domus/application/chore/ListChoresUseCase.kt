package com.domus.application.chore

import com.domus.core.chore.Chore
import com.domus.core.chore.ChoreRepository
import org.springframework.stereotype.Service

@Service
open class ListChoresUseCase(val choreRepository: ChoreRepository) {

    fun getChores(): List<Chore> {
        return choreRepository.findAll()
    }

    fun addChore(chore: Chore) {
        choreRepository.save(chore)
    }
}