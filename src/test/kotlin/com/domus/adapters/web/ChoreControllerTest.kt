package com.domus.adapters.web

import com.domus.core.chore.Chore
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(ChoreController::class)
class ChoreControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `getChores returns list of chores`() {
        val expected = """[{"name":"Placeholder chore"}]"""

        mockMvc.perform(get("/api/chores"))
            .andExpect(status().isOk)
            .andExpect(content().json(expected))
    }
}
