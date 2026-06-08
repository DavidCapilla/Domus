package com.domus.adapters.web

import org.junit.jupiter.api.BeforeEach
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
import java.time.LocalDate

@WebMvcTest(ChoreController::class)
@Import(ChoreControllerTestConfig::class)
class ChoreControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var repository: FakeChoreRepository

    @BeforeEach
    fun setUp() {
        repository.reset()
    }

    @Test
    fun `getChores returns list of chores`() {
        mockMvc.perform(get("/api/chores"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[?(@.name=='Placeholder chore')]").exists())
    }

    @Test
    fun `completeChore returns 200 for existing chore`() {
        mockMvc.perform(post("/api/chores/{name}/complete", "Placeholder chore"))
            .andExpect(status().isOk)
    }

    @Test
    fun `completeChore returns 404 for non-existent chore`() {
        mockMvc.perform(post("/api/chores/{name}/complete", "Non-existent"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `addChore returns 200 for new chore`() {
        val dueDate = LocalDate.now().plusDays(14).toString()
        val json = """{"name":"New chore","dueDate":"$dueDate"}"""

        mockMvc.perform(
            post("/api/chores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `addChore returns 409 for duplicate chore`() {
        val dueDate = LocalDate.now().plusDays(21).toString()
        val json = """{"name":"Placeholder chore","dueDate":"$dueDate"}"""

        mockMvc.perform(
            post("/api/chores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isConflict)
    }

    @Test
    fun `updateChore returns 200 for existing chore`() {
        val dueDate = LocalDate.now().plusDays(7).toString()
        val json = """{"name":"Placeholder chore","dueDate":"$dueDate"}"""

        mockMvc.perform(
            put("/api/chores/{name}", "Placeholder chore")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Placeholder chore"))
    }

    @Test
    fun `updateChore returns 404 for non-existent chore`() {
        val dueDate = LocalDate.now().plusDays(7).toString()
        val json = """{"name":"Anything","dueDate":"$dueDate"}"""

        mockMvc.perform(
            put("/api/chores/{name}", "Non-existent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `deleteChore returns 200 when chore exists`() {
        mockMvc.perform(delete("/api/chores/{name}", "Placeholder chore"))
            .andExpect(status().isOk)
    }

    @Test
    fun `deleteChore returns 404 when chore not found`() {
        mockMvc.perform(delete("/api/chores/{name}", "Non-existent"))
            .andExpect(status().isNotFound)
    }
}