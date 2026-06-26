package com.domus.chores

import com.domus.chores.adapters.web.dto.ChoreResponse
import com.domus.chores.core.ChoreName
import com.domus.chores.core.ChoreRepository
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
    private lateinit var repository: ChoreRepository

    private lateinit var cleanKitchenId: java.util.UUID
    private lateinit var doLaundryId: java.util.UUID

    @BeforeEach
    fun setUp() {
        repository.findAll().forEach { repository.delete(it.id) }
        repository.save(createChore(name = "Clean kitchen", dueDate = LocalDate.now().plusDays(5)))
        repository.save(createChore(name = "Do laundry", dueDate = LocalDate.now().plusDays(10)))
        cleanKitchenId = repository.findByName(ChoreName.of("Clean kitchen"))!!.id
        doLaundryId = repository.findByName(ChoreName.of("Do laundry"))!!.id
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
            "/api/chores/{id}",
            HttpMethod.PUT,
            HttpEntity("""{"name":"Clean kitchen","dueDate":"$dueDate"}""", headers),
            ChoreResponse::class.java,
            cleanKitchenId,
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
            "/api/chores/{id}",
            HttpMethod.PUT,
            HttpEntity("""{"name":"Anything","dueDate":"$dueDate"}""", headers),
            String::class.java,
            "00000000-0000-0000-0000-000000000000",
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `completeChore deletes one-time chore`() {
        val oneOff = createChore(name = "One-off task", schedule = Schedule.OneTime)
        repository.save(oneOff)
        val oneOffId = repository.findByName(ChoreName.of("One-off task"))!!.id

        val response = restTemplate.exchange(
            "/api/chores/{id}/complete",
            HttpMethod.POST,
            null,
            String::class.java,
            oneOffId,
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(repository.findByName(ChoreName.of("One-off task"))).isNull()
    }

    @Test
    fun `completeChore reschedules every-N-days chore`() {
        val recurring = createChore(name = "Recurring task", schedule = Schedule.EveryNDays(7))
        repository.save(recurring)
        val recurringId = repository.findByName(ChoreName.of("Recurring task"))!!.id

        val response = restTemplate.exchange(
            "/api/chores/{id}/complete",
            HttpMethod.POST,
            null,
            String::class.java,
            recurringId,
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val updated = repository.findById(recurringId)
        assertThat(updated).isNotNull
        assertThat(updated!!.dueDate).isEqualTo(LocalDate.now().plusDays(7))
    }

    @Test
    fun `completeChore returns 404 for non-existent chore`() {
        val response = restTemplate.exchange(
            "/api/chores/{id}/complete",
            HttpMethod.POST,
            null,
            String::class.java,
            "00000000-0000-0000-0000-000000000000",
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `deleteChore returns 200 when chore exists`() {
        val response = restTemplate.exchange(
            "/api/chores/{id}",
            HttpMethod.DELETE,
            null,
            String::class.java,
            cleanKitchenId,
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `deleteChore returns 404 when chore not found`() {
        val response = restTemplate.exchange(
            "/api/chores/{id}",
            HttpMethod.DELETE,
            null,
            String::class.java,
            "00000000-0000-0000-0000-000000000000",
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `addChore with trailing space saves trimmed name and allows update`() {
        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }
        val dueDate = LocalDate.now().plusDays(10).toString()

        val postResponse = restTemplate.postForEntity(
            "/api/chores",
            HttpEntity("""{"name":" Take out trash ","dueDate":"$dueDate"}""", headers),
            String::class.java,
        )
        assertThat(postResponse.statusCode).isEqualTo(HttpStatus.OK)

        val trimmed = repository.findByName(ChoreName.of("Take out trash"))
        assertThat(trimmed).isNotNull

        val trimmedId = trimmed!!.id
        val updateResponse = restTemplate.exchange(
            "/api/chores/{id}",
            HttpMethod.PUT,
            HttpEntity("""{"name":"Take out trash","dueDate":"$dueDate"}""", headers),
            ChoreResponse::class.java,
            trimmedId,
        )
        assertThat(updateResponse.statusCode).isEqualTo(HttpStatus.OK)
    }
}
