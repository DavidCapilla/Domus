package com.domus.habits

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
class HabitIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `full habit lifecycle via web endpoints`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/habits")
                .param("name", "Read")
                .param("targetCount", "3")
                .param("timeWindowType", "weekly"),
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

        mockMvc.perform(MockMvcRequestBuilders.get("/habits"))
            .andExpect(MockMvcResultMatchers.status().isOk)

        mockMvc.perform(MockMvcRequestBuilders.post("/habits/Read/log"))
            .andExpect(MockMvcResultMatchers.status().isOk)

        mockMvc.perform(MockMvcRequestBuilders.delete("/habits/Read"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `full habit lifecycle via REST endpoints`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/habits")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"Run","targetCount":1,"timeWindow":{"type":"daily"}}"""),
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Run"))

        mockMvc.perform(MockMvcRequestBuilders.get("/api/habits"))
            .andExpect(MockMvcResultMatchers.status().isOk)

        mockMvc.perform(MockMvcRequestBuilders.post("/api/habits/Run/log"))
            .andExpect(MockMvcResultMatchers.status().isOk)

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/habits/Run"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }
}
