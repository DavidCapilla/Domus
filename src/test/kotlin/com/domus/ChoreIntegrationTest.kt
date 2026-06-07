package com.domus

import com.domus.adapters.persistence.InMemoryChoreRepository
import com.domus.adapters.web.dto.ChoreResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ChoreIntegrationTest {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var repository: InMemoryChoreRepository

    @BeforeEach
    fun setUp() {
        repository.findAll().forEach { repository.delete(it.name) }
        repository.save(createChore("Clean kitchen"))
        repository.save(createChore("Do laundry"))
    }

    @Test
    fun `getChores returns 200 and list with seeded chores`() {
        val response = restTemplate.getForEntity("/api/chores", Array<ChoreResponse>::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body!!.map { it.name })
            .containsExactlyInAnyOrder("Clean kitchen", "Do laundry")
    }

    @Test
    fun `addChore returns 200 for new chore`() {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }
        val response = restTemplate.postForEntity(
            "/api/chores",
            HttpEntity("""{"name":"New chore"}""", headers),
            String::class.java,
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `addChore returns 409 for duplicate chore`() {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }
        val response = restTemplate.postForEntity(
            "/api/chores",
            HttpEntity("""{"name":"Clean kitchen"}""", headers),
            String::class.java,
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.CONFLICT)
    }

    @Test
    fun `deleteChore returns 200 when chore exists`() {
        val response = restTemplate.exchange(
            "/api/chores/{name}",
            HttpMethod.DELETE,
            null,
            String::class.java,
            "Clean kitchen",
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `deleteChore returns 404 when chore not found`() {
        val response = restTemplate.exchange(
            "/api/chores/{name}",
            HttpMethod.DELETE,
            null,
            String::class.java,
            "Non-existent",
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }
}