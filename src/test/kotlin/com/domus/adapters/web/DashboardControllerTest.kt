package com.domus.adapters.web

import com.domus.createChore
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

@WebMvcTest(DashboardController::class)
@Import(DashboardControllerTestConfig::class)
class DashboardControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var repository: FakeChoreRepository

    @BeforeEach
    fun setUp() {
        repository.reset()
        repository.findAll().forEach { repository.delete(it.name) }
    }

    @Test
    fun `getDashboard returns grouped chores`() {
        repository.save(createChore(name = "Overdue", dueDate = LocalDate.of(2026, 6, 8)))
        repository.save(createChore(name = "Due today", dueDate = LocalDate.of(2026, 6, 9)))
        repository.save(createChore(name = "Upcoming", dueDate = LocalDate.of(2026, 6, 10)))

        mockMvc.perform(get("/api/dashboard"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.overdue.length()").value(1))
            .andExpect(jsonPath("$.overdue[0].name").value("Overdue"))
            .andExpect(jsonPath("$.dueToday.length()").value(1))
            .andExpect(jsonPath("$.dueToday[0].name").value("Due today"))
            .andExpect(jsonPath("$.upcoming.length()").value(1))
            .andExpect(jsonPath("$.upcoming[0].name").value("Upcoming"))
    }

    @Test
    fun `getDashboard returns empty when no chores exist`() {
        mockMvc.perform(get("/api/dashboard"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.overdue").isEmpty())
            .andExpect(jsonPath("$.dueToday").isEmpty())
            .andExpect(jsonPath("$.upcoming").isEmpty())
    }
}
