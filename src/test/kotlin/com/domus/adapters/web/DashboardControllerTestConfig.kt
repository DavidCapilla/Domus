package com.domus.adapters.web

import com.domus.application.dashboard.GetDashboardUseCase
import com.domus.core.chore.ChoreRepository
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

@TestConfiguration
class DashboardControllerTestConfig {

    @Bean
    fun clock(): Clock = Clock.fixed(
        Instant.parse("2026-06-09T00:00:00Z"),
        ZoneOffset.UTC,
    )

    @Bean
    fun choreRepository(): ChoreRepository = FakeChoreRepository()

    @Bean
    fun getDashboardUseCase(repo: ChoreRepository, clock: Clock): GetDashboardUseCase =
        GetDashboardUseCase(repo, clock)
}
