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
import org.springframework.test.annotation.DirtiesContext

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ChoreIntegrationTest {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    fun `getChores returns 200 and list with seeded chores`() {
        val response = restTemplate.getForEntity("/api/chores", Array<Chore>::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body!!.map { it.name })
            .containsExactlyInAnyOrder("Clean kitchen", "Do laundry")
    }

    @Test
    fun `addChore returns 200 for new chore`() {
        val response = restTemplate.postForEntity("/api/chore", Chore(name = "New chore"), String::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `addChore returns 409 for duplicate chore`() {
        val response = restTemplate.postForEntity("/api/chore", Chore(name = "Clean kitchen"), String::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.CONFLICT)
    }

    @TestConfiguration
    class SeedDataConfig {

        @Bean
        @Primary
        fun choreRepository(): ChoreRepository {
            val repo = InMemoryChoreRepository()
            repo.save(Chore(name = "Clean kitchen"))
            repo.save(Chore(name = "Do laundry"))
            return repo
        }
    }
}