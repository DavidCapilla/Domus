package com.domus.adapters.web

import com.domus.application.chore.CompleteChoreUseCase
import com.domus.application.chore.CreateChoreUseCase
import com.domus.application.chore.DeleteChoreUseCase
import com.domus.application.chore.ListChoresUseCase
import com.domus.application.chore.UpdateChoreUseCase
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