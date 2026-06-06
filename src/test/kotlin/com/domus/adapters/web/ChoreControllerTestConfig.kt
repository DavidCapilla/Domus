package com.domus.adapters.web

import com.domus.application.chore.CreateChoreUseCase
import com.domus.application.chore.ListChoresUseCase
import com.domus.core.chore.Chore
import com.domus.core.chore.ChoreRepository
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class ChoreControllerTestConfig {

    @Bean
    fun choreRepository(): ChoreRepository =
        FakeChoreRepository()

    @Bean
    fun listChoresUseCase(repo: ChoreRepository): ListChoresUseCase =
        ListChoresUseCase(repo)

    @Bean
    fun createChoreUseCase(repo: ChoreRepository): CreateChoreUseCase =
        CreateChoreUseCase(repo)
}

class FakeChoreRepository : ChoreRepository {

    private val chores = mutableListOf(Chore(name = "Placeholder chore"))

    override fun findAll(): List<Chore> = chores.toList()

    override fun save(chore: Chore): Boolean {
        val alreadyExists = chores.any { it == chore }
        if (!alreadyExists) chores.add(chore)
        return !alreadyExists
    }
}