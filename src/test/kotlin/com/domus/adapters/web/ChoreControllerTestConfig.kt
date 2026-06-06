package com.domus.adapters.web

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
}

class FakeChoreRepository : ChoreRepository {
    override fun findAll(): List<Chore> =
        listOf(Chore(name = "Placeholder chore"))
}