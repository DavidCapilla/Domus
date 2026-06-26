package com.domus.chores.adapters.web

import com.domus.chores.application.CompleteChoreUseCase
import com.domus.chores.application.CreateChoreUseCase
import com.domus.chores.application.DeleteChoreUseCase
import com.domus.chores.application.GetChoreUseCase
import com.domus.chores.application.GetDashboardUseCase
import com.domus.chores.application.ListChoresUseCase
import com.domus.chores.application.UpdateChoreUseCase
import com.domus.chores.core.ChoreRepository
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

@TestConfiguration
class ChoreWebControllerTestConfig {

    @Bean
    fun clock(): Clock = Clock.fixed(
        Instant.parse("2026-06-09T00:00:00Z"),
        ZoneOffset.UTC,
    )

    @Bean
    fun choreRepository(): ChoreRepository = FakeChoreRepository()

    @Bean
    fun getChoreUseCase(repo: ChoreRepository): GetChoreUseCase =
        GetChoreUseCase(repo)

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

    @Bean
    fun getDashboardUseCase(repo: ChoreRepository, clock: Clock): GetDashboardUseCase =
        GetDashboardUseCase(repo, clock)
}
