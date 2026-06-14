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
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

@WebMvcTest(HabitWebController::class)
@Import(HabitWebControllerTest.TestConfig::class)
class HabitWebControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var habitRepository: HabitRepository

    @Autowired
    private lateinit var habitLogRepository: HabitLogRepository

    @TestConfiguration
    class TestConfig {
        @Bean
        fun habitWebController() = HabitWebController(
            listHabitsUseCase(),
            getHabitProgressUseCase(),
            createHabitUseCase(),
            updateHabitUseCase(),
            logHabitCompletionUseCase(),
            deleteHabitUseCase(),
        )

        @Bean
        fun createHabitUseCase() = CreateHabitUseCase(habitRepository())

        @Bean
        fun listHabitsUseCase() = ListHabitsUseCase(habitRepository())

        @Bean
        fun deleteHabitUseCase() = DeleteHabitUseCase(habitRepository())

        @Bean
        fun updateHabitUseCase() = UpdateHabitUseCase(habitRepository())

        @Bean
        fun logHabitCompletionUseCase() = LogHabitCompletionUseCase(
            habitRepository(),
            habitLogRepository(),
            Clock.fixed(Instant.parse("2026-06-10T10:00:00Z"), ZoneId.of("UTC")),
        )

        @Bean
        fun getHabitProgressUseCase() = GetHabitProgressUseCase(
            habitRepository(),
            habitLogRepository(),
            Clock.fixed(Instant.parse("2026-06-10T10:00:00Z"), ZoneId.of("UTC")),
        )

        @Bean
        fun habitRepository(): HabitRepository = InMemoryHabitRepository()

        @Bean
        fun habitLogRepository(): HabitLogRepository = InMemoryHabitLogRepository()
    }

    @AfterEach
    fun tearDown() {
        habitRepository.findAll().forEach { habitRepository.delete(it.name) }
    }

    @Test
    fun `GET habits returns page`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/habits"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.xpath("/html/body").exists())
    }

    @Test
    fun `POST habits creates habit`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/habits")
                .param("name", "Read")
                .param("targetCount", "1")
                .param("timeWindowType", "daily"),
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `POST habits with blank name returns error`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/habits")
                .param("name", "")
                .param("targetCount", "1")
                .param("timeWindowType", "daily"),
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(
                MockMvcResultMatchers.xpath("//*[contains(text(), 'Name is required')]").exists(),
            )
    }

    @Test
    fun `POST habits with duplicate name returns 409`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/habits")
                .param("name", "Read")
                .param("targetCount", "1")
                .param("timeWindowType", "daily"),
        )
        mockMvc.perform(
            MockMvcRequestBuilders.post("/habits")
                .param("name", "Read")
                .param("targetCount", "1")
                .param("timeWindowType", "daily"),
        )
            .andExpect(MockMvcResultMatchers.status().isConflict)
    }

    @Test
    fun `POST habits-name-log logs completion`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/habits")
                .param("name", "Read")
                .param("targetCount", "1")
                .param("timeWindowType", "daily"),
        )
        mockMvc.perform(MockMvcRequestBuilders.post("/habits/Read/log"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `DELETE habits-name deletes habit`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/habits")
                .param("name", "Read")
                .param("targetCount", "1")
                .param("timeWindowType", "daily"),
        )
        mockMvc.perform(MockMvcRequestBuilders.delete("/habits/Read"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `GET habits-name-edit returns edit form`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/habits")
                .param("name", "Read")
                .param("targetCount", "1")
                .param("timeWindowType", "daily"),
        )
        mockMvc.perform(MockMvcRequestBuilders.get("/habits/Read/edit"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.xpath("//form").exists())
    }

    @Test
    fun `PUT habits-name updates habit`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/habits")
                .param("name", "Read")
                .param("targetCount", "1")
                .param("timeWindowType", "daily"),
        )
        mockMvc.perform(
            MockMvcRequestBuilders.put("/habits/Read")
                .param("newName", "Reading")
                .param("targetCount", "3")
                .param("timeWindowType", "weekly"),
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `GET habits list returns fragment`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/habits/list"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }
}
