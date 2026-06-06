package com.domus

import com.domus.adapters.persistence.InMemoryChoreRepository
import com.domus.core.chore.Chore
import com.domus.core.chore.ChoreRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpStatus
import java.util.UUID

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ChoreIntegrationTest {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun `getChores returns 200 and list with seeded chores`() {
        val response = restTemplate.getForEntity("/api/chores", Array<Chore>::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).hasSize(2)
        assertThat(response.body!![0].name).isEqualTo("Clean kitchen")
        assertThat(response.body!![1].name).isEqualTo("Do laundry")
    }

    @TestConfiguration
    class SeedDataConfig {

        @Bean
        @Primary
        fun choreRepository(): ChoreRepository {
            return InMemoryChoreRepository(
                mapOf(
                    UUID.randomUUID() to Chore(name = "Clean kitchen"),
                    UUID.randomUUID() to Chore(name = "Do laundry")
                )
            )
        }
    }
}
