package com.domus.application.chore

import com.domus.core.chore.Chore
import com.domus.core.chore.ChoreRepository

class ListChoresUseCase (val choreRepository: ChoreRepository) {

    fun getChores(): List<Chore> {
        return choreRepository.findAll()
    }
}