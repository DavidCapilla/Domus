package com.domus.chores.adapters.web

import com.domus.chores.application.CompleteChoreUseCase
import com.domus.chores.application.CreateChoreUseCase
import com.domus.chores.application.DeleteChoreUseCase
import com.domus.chores.application.ListChoresUseCase
import com.domus.chores.application.UpdateChoreUseCase
import com.domus.chores.core.ChoreRepository
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

    @Bean
    fun updateChoreUseCase(repo: ChoreRepository): UpdateChoreUseCase =
        UpdateChoreUseCase(repo)

    @Bean
    fun completeChoreUseCase(repo: ChoreRepository): CompleteChoreUseCase =
        CompleteChoreUseCase(repo)

    @Bean
    fun deleteChoreUseCase(repo: ChoreRepository): DeleteChoreUseCase =
        DeleteChoreUseCase(repo)
}