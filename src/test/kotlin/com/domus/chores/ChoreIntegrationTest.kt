package com.domus.chores

import com.domus.chores.adapters.persistence.InMemoryChoreRepository
import com.domus.chores.adapters.web.dto.ChoreResponse
import com.domus.chores.core.Schedule
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
import java.time.LocalDate

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ChoreIntegrationTest {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var repository: InMemoryChoreRepository

    @BeforeEach
    fun setUp() {
        repository.findAll().forEach { repository.delete(it.name) }
        repository.save(createChore(name = "Clean kitchen", dueDate = LocalDate.now().plusDays(5)))
        repository.save(createChore(name = "Do laundry", dueDate = LocalDate.now().plusDays(10)))
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
        val dueDate = LocalDate.now().plusDays(30).toString()
        val response = restTemplate.postForEntity(
            "/api/chores",
            HttpEntity("""{"name":"New chore","dueDate":"$dueDate"}""", headers),
            String::class.java,
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `addChore returns 409 for duplicate chore`() {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }
        val dueDate = LocalDate.now().plusDays(5).toString()
        val response = restTemplate.postForEntity(
            "/api/chores",
            HttpEntity("""{"name":"Clean kitchen","dueDate":"$dueDate"}""", headers),
            String::class.java,
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.CONFLICT)
    }

    @Test
    fun `updateChore returns 200 and updates chore`() {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }
        val dueDate = LocalDate.now().plusDays(15).toString()
        val response = restTemplate.exchange(
            "/api/chores/{name}",
            HttpMethod.PUT,
            HttpEntity("""{"name":"Clean kitchen","dueDate":"$dueDate"}""", headers),
            ChoreResponse::class.java,
            "Clean kitchen",
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body!!.name).isEqualTo("Clean kitchen")
        assertThat(response.body!!.dueDate.toString()).isEqualTo(dueDate)
    }

    @Test
    fun `updateChore returns 404 for non-existent chore`() {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }
        val dueDate = LocalDate.now().plusDays(15).toString()
        val response = restTemplate.exchange(
            "/api/chores/{name}",
            HttpMethod.PUT,
            HttpEntity("""{"name":"Anything","dueDate":"$dueDate"}""", headers),
            String::class.java,
            "Non-existent",
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `completeChore deletes one-time chore`() {
        repository.save(createChore(name = "One-off task", schedule = Schedule.OneTime))

        val response = restTemplate.exchange(
            "/api/chores/{name}/complete",
            HttpMethod.POST,
            null,
            String::class.java,
            "One-off task",
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(repository.findByName("One-off task")).isNull()
    }

    @Test
    fun `completeChore reschedules every-N-days chore`() {
        repository.save(createChore(name = "Recurring task", schedule = Schedule.EveryNDays(7)))

        val response = restTemplate.exchange(
            "/api/chores/{name}/complete",
            HttpMethod.POST,
            null,
            String::class.java,
            "Recurring task",
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val updated = repository.findByName("Recurring task")
        assertThat(updated).isNotNull
        assertThat(updated!!.dueDate).isEqualTo(LocalDate.now().plusDays(7))
    }

    @Test
    fun `completeChore returns 404 for non-existent chore`() {
        val response = restTemplate.exchange(
            "/api/chores/{name}/complete",
            HttpMethod.POST,
            null,
            String::class.java,
            "Non-existent",
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
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