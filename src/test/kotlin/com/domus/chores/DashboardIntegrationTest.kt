package com.domus.chores

import com.domus.chores.adapters.persistence.InMemoryChoreRepository
import com.domus.chores.adapters.web.dto.DashboardResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import java.time.LocalDate

@SpringBootTest(webEnvironment = RANDOM_PORT)
class DashboardIntegrationTest {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var repository: InMemoryChoreRepository

    @BeforeEach
    fun setUp() {
        repository.findAll().forEach { repository.delete(it.name) }
        repository.save(createChore(name = "Overdue chore", dueDate = LocalDate.now().minusDays(2)))
        repository.save(
            createChore(
                name = "Another overdue chore",
                dueDate = LocalDate.now().minusDays(1)
            )
        )
        repository.save(createChore(name = "Due today chore", dueDate = LocalDate.now()))
        repository.save(createChore(name = "Upcoming chore", dueDate = LocalDate.now().plusDays(3)))
    }

    @Test
    fun `getDashboard returns 200 with grouped chores`() {
        val response = restTemplate.getForEntity("/api/dashboard", DashboardResponse::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body!!.overdue.map { it.name }).containsExactlyInAnyOrder(
            "Overdue chore",
            "Another overdue chore"
        )
        assertThat(response.body!!.dueToday.map { it.name }).containsExactly("Due today chore")
        assertThat(response.body!!.upcoming.map { it.name }).containsExactly("Upcoming chore")
    }

    @Test
    fun `getDashboard returns empty lists when no chores exist`() {
        repository.findAll().forEach { repository.delete(it.name) }

        val response = restTemplate.getForEntity("/api/dashboard", DashboardResponse::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body!!.overdue).isEmpty()
        assertThat(response.body!!.dueToday).isEmpty()
        assertThat(response.body!!.upcoming).isEmpty()
    }
}
