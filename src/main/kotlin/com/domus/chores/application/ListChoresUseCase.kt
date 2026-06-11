package com.domus.chores.application

import com.domus.chores.core.Chore
import com.domus.chores.core.ChoreRepository
import org.springframework.stereotype.Service

@Service
open class ListChoresUseCase(val choreRepository: ChoreRepository) {

    fun getChores(): List<Chore> {
        return choreRepository.findAll()
    }
}