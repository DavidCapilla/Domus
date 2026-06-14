package com.domus.habits.adapters.web

import com.domus.habits.adapters.persistence.InMemoryHabitLogRepository
import com.domus.habits.adapters.persistence.InMemoryHabitRepository
import com.domus.habits.application.CreateHabitUseCase
import com.domus.habits.application.DeleteHabitUseCase
import com.domus.habits.application.GetHabitProgressUseCase
import com.domus.habits.application.ListHabitsUseCase
import com.domus.habits.application.LogHabitCompletionUseCase
import com.domus.habits.application.UpdateHabitUseCase
import com.domus.habits.core.HabitLogRepository
import com.domus.habits.core.HabitRepository
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

@TestConfiguration
class HabitControllerTestConfig {

    @Bean
    fun habitRepository(): HabitRepository = InMemoryHabitRepository()

    @Bean
    fun habitLogRepository(): HabitLogRepository = InMemoryHabitLogRepository()

    @Bean
    fun listHabitsUseCase(repo: HabitRepository) = ListHabitsUseCase(repo)

    @Bean
    fun createHabitUseCase(repo: HabitRepository) = CreateHabitUseCase(repo)

    @Bean
    fun deleteHabitUseCase(repo: HabitRepository) = DeleteHabitUseCase(repo)

    @Bean
    fun updateHabitUseCase(repo: HabitRepository) = UpdateHabitUseCase(repo)

    @Bean
    fun logHabitCompletionUseCase(repo: HabitRepository, logRepo: HabitLogRepository) =
        LogHabitCompletionUseCase(repo, logRepo, Clock.fixed(Instant.parse("2026-06-10T10:00:00Z"), ZoneId.of("UTC")))

    @Bean
    fun getHabitProgressUseCase(repo: HabitRepository, logRepo: HabitLogRepository) =
        GetHabitProgressUseCase(repo, logRepo, Clock.fixed(Instant.parse("2026-06-10T10:00:00Z"), ZoneId.of("UTC")))
}
