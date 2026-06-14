package com.domus.habits.adapters.web

import com.domus.habits.adapters.web.dto.HabitRequest
import com.domus.habits.adapters.web.dto.TimeWindowDto
import com.domus.habits.core.HabitRepository
import com.domus.habits.createHabit
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(HabitController::class)
@Import(HabitControllerTestConfig::class)
class HabitControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var habitRepository: HabitRepository

    @AfterEach
    fun tearDown() {
        habitRepository.findAll().forEach { habitRepository.delete(it.name) }
    }

    @Test
    fun `GET api-habits returns list`() {
        habitRepository.save(createHabit(name = "Read"))

        mockMvc.perform(get("/api/habits"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.size()").value(1))
            .andExpect(jsonPath("$[0].name").value("Read"))
    }

    @Test
    fun `GET api-habits returns empty when no habits`() {
        mockMvc.perform(get("/api/habits"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.size()").value(0))
    }

    @Test
    fun `POST api-habits creates habit`() {
        mockMvc.perform(
            post("/api/habits")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"Read","targetCount":1,"timeWindow":{"type":"daily"}}"""),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("Read"))
    }

    @Test
    fun `POST api-habits returns 409 on duplicate`() {
        habitRepository.save(createHabit(name = "Read"))

        mockMvc.perform(
            post("/api/habits")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"Read","targetCount":1,"timeWindow":{"type":"daily"}}"""),
        )
            .andExpect(status().isConflict)
    }

    @Test
    fun `PUT api-habits-name updates habit`() {
        habitRepository.save(createHabit(name = "Read"))

        mockMvc.perform(
            put("/api/habits/Read")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"Reading","description":"30 min","targetCount":5,"timeWindow":{"type":"monthly"}}"""),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Reading"))
    }

    @Test
    fun `DELETE api-habits-name deletes habit`() {
        habitRepository.save(createHabit(name = "Read"))

        mockMvc.perform(delete("/api/habits/Read"))
            .andExpect(status().isOk)
    }

    @Test
    fun `POST api-habits-name-log creates log`() {
        val habit = createHabit(name = "Read")
        habitRepository.save(habit)

        mockMvc.perform(post("/api/habits/Read/log"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Read"))
    }
}
