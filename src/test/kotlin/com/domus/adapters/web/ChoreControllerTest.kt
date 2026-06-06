package com.domus.adapters.web

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(ChoreController::class)
@Import(ChoreControllerTestConfig::class)
class ChoreControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `getChores returns list of chores`() {
        mockMvc.perform(get("/api/chores"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].name").value("Placeholder chore"))
    }
}
