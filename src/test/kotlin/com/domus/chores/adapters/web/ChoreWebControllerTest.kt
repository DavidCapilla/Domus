package com.domus.chores.adapters.web

import com.domus.chores.createChore
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

@WebMvcTest(ChoreWebController::class)
@Import(ChoreWebControllerTestConfig::class)
class ChoreWebControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var repository: FakeChoreRepository

    @BeforeEach
    fun setUp() {
        repository.reset()
    }

    @Nested
    inner class DashboardPage {

        @Test
        fun `renders full page with title and add button`() {
            mockMvc.perform(get("/"))
                .andExpect(status().isOk)
                .andExpect(content().string(containsString("Chores Dashboard")))
                .andExpect(content().string(containsString("openModal")))
        }

        @Test
        fun `renders modal with form fields`() {
            mockMvc.perform(get("/"))
                .andExpect(status().isOk)
                .andExpect(content().string(containsString("add-chore-modal")))
                .andExpect(content().string(containsString("name=\"name\"")))
                .andExpect(content().string(containsString("name=\"dueDate\"")))
                .andExpect(content().string(containsString("value=\"one_time\"")))
                .andExpect(content().string(containsString("value=\"every_n_days\"")))
                .andExpect(content().string(containsString("name=\"days\"")))
        }

        @Test
        fun `modal form posts to correct endpoint`() {
            mockMvc.perform(get("/"))
                .andExpect(content().string(containsString("hx-post=\"/chores\"")))
                .andExpect(content().string(containsString("hx-target=\"#chore-list\"")))
                .andExpect(content().string(containsString("hx-swap=\"outerHTML\"")))
        }

        @Test
        fun `days field defaults to 7 and is hidden by default`() {
            mockMvc.perform(get("/"))
                .andExpect(content().string(containsString("value=\"7\"")))
                .andExpect(content().string(containsString("display:none")))
        }

        @Test
        fun `recurring days group is in the add form`() {
            mockMvc.perform(get("/"))
                .andExpect(content().string(containsString("recurring-days")))
                .andExpect(content().string(containsString("style=\"display:none\"")))
                .andExpect(content().string(containsString("every")))
                .andExpect(content().string(containsString("days")))
        }

        @Test
        fun `renders overdue section with chore names`() {
            repository.save(createChore(name = "Overdue chore", dueDate = LocalDate.of(2026, 6, 8)))

            mockMvc.perform(get("/"))
                .andExpect(content().string(containsString("Overdue")))
                .andExpect(content().string(containsString("Overdue chore")))
        }

        @Test
        fun `renders due today and upcoming sections`() {
            repository.save(createChore(name = "Today chore", dueDate = LocalDate.of(2026, 6, 9)))
            repository.save(createChore(name = "Future chore", dueDate = LocalDate.of(2026, 6, 15)))

            mockMvc.perform(get("/"))
                .andExpect(content().string(containsString("Due Today")))
                .andExpect(content().string(containsString("Today chore")))
                .andExpect(content().string(containsString("Upcoming")))
                .andExpect(content().string(containsString("Future chore")))
        }

        @Test
        fun `shows empty message when no chores`() {
            repository.allChores.forEach { repository.delete(it.id) }

            mockMvc.perform(get("/"))
                .andExpect(content().string(containsString("No overdue chores")))
                .andExpect(content().string(containsString("No chores due today")))
                .andExpect(content().string(containsString("No upcoming chores")))
        }

        @Test
        fun `modal is hidden by default`() {
            mockMvc.perform(get("/"))
                .andExpect(content().string(containsString("add-chore-modal")))
                .andExpect(content().string(containsString("hidden")))
        }

        @Test
        fun `detail modal is present in page`() {
            mockMvc.perform(get("/"))
                .andExpect(content().string(containsString("detail-chore-modal")))
                .andExpect(content().string(containsString("Chore details")))
                .andExpect(content().string(containsString("closeDetailModal")))
        }

        @Test
        fun `cards have status data attribute`() {
            repository.save(createChore(name = "Badge test chore", dueDate = LocalDate.of(2026, 6, 2)))

            mockMvc.perform(get("/"))
                .andExpect(content().string(containsString("data-status=\"overdue\"")))
                .andExpect(content().string(containsString("Overdue")))
        }

    }

    @Nested
    inner class ChoreListFragment {

        @Test
        fun `returns only the chore-list fragment`() {
            mockMvc.perform(get("/chores"))
                .andExpect(status().isOk)
                .andExpect(content().string(containsString("chore-list")))
                .andExpect(content().string(not(containsString("<html"))))
        }

        @Test
        fun `lists chores in the fragment`() {
            mockMvc.perform(get("/chores"))
                .andExpect(content().string(containsString("Placeholder chore")))
        }

        @Test
        fun `fragment contains overdue today and upcoming sections`() {
            mockMvc.perform(get("/chores"))
                .andExpect(content().string(containsString("Overdue")))
                .andExpect(content().string(containsString("Due Today")))
                .andExpect(content().string(containsString("Upcoming")))
        }
    }

    @Nested
    inner class AddChore {

        @Test
        fun `creates chore and returns updated fragment`() {
            mockMvc.perform(post("/chores")
                .param("name", "New chore")
                .param("dueDate", "2026-06-20")
                .param("scheduleType", "one_time"))
                .andExpect(status().isOk)
                .andExpect(content().string(containsString("New chore")))
                .andExpect(content().string(containsString("Placeholder chore")))
        }

        @Test
        fun `creates recurring chore with every_n_days`() {
            mockMvc.perform(post("/chores")
                .param("name", "Recurring chore")
                .param("dueDate", "2026-06-20")
                .param("scheduleType", "every_n_days")
                .param("days", "3"))
                .andExpect(status().isOk)
                .andExpect(content().string(containsString("Recurring chore")))
        }

        @Test
        fun `returns error when name is blank`() {
            mockMvc.perform(post("/chores")
                .param("name", "")
                .param("dueDate", "2026-06-20")
                .param("scheduleType", "one_time"))
                .andExpect(header().string("HX-Trigger", containsString("Name is required")))
        }

        @Test
        fun `returns error when days is invalid`() {
            mockMvc.perform(post("/chores")
                .param("name", "Bad chore")
                .param("dueDate", "2026-06-20")
                .param("scheduleType", "every_n_days")
                .param("days", ""))
                .andExpect(header().string("HX-Trigger", containsString("Days must be a positive number")))
        }

        @Test
        fun `returns error when days is zero`() {
            mockMvc.perform(post("/chores")
                .param("name", "Bad chore")
                .param("dueDate", "2026-06-20")
                .param("scheduleType", "every_n_days")
                .param("days", "0"))
                .andExpect(header().string("HX-Trigger", containsString("Days must be a positive number")))
        }

        @Test
        fun `returns error message when chore already exists`() {
            mockMvc.perform(post("/chores")
                .param("name", "Placeholder chore")
                .param("dueDate", "2026-06-20")
                .param("scheduleType", "one_time"))
                .andExpect(status().isOk)
                .andExpect(header().string("HX-Trigger", containsString("A chore with this name already exists")))
        }

        @Test
        fun `returns fragment without html wrapping`() {
            mockMvc.perform(post("/chores")
                .param("name", "Fragment test")
                .param("dueDate", "2026-06-20")
                .param("scheduleType", "one_time"))
                .andExpect(content().string(not(containsString("<html"))))
        }
    }

    @Nested
    inner class EditChore {

        @Test
        fun `returns edit form with pre-filled name`() {
            val id = repository.allChores.first().id
            mockMvc.perform(get("/chores/{id}/edit", id))
                .andExpect(status().isOk)
                .andExpect(content().string(containsString("value=\"Placeholder chore\"")))
                .andExpect(content().string(containsString("hx-put=\"/chores/$id\"")))
        }

        @Test
        fun `edit form has save and cancel buttons`() {
            val id = repository.allChores.first().id
            mockMvc.perform(get("/chores/{id}/edit", id))
                .andExpect(content().string(containsString("Save")))
                .andExpect(content().string(containsString("Cancel")))
        }

        @Test
        fun `returns error message for non-existent chore`() {
            mockMvc.perform(get("/chores/{id}/edit", "00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isOk)
                .andExpect(content().string(containsString("Chore not found")))
        }
    }

    @Nested
    inner class UpdateChore {

        @Test
        fun `updates chore and returns fragment`() {
            val id = repository.allChores.first().id
            mockMvc.perform(put("/chores/{id}", id)
                .param("name", "Updated chore")
                .param("dueDate", "2026-06-25")
                .param("scheduleType", "one_time"))
                .andExpect(status().isOk)
                .andExpect(content().string(not(containsString("Placeholder chore"))))
                .andExpect(content().string(containsString("Updated chore")))
        }

        @Test
        fun `returns error when new name is blank`() {
            val id = repository.allChores.first().id
            mockMvc.perform(put("/chores/{id}", id)
                .param("name", "")
                .param("dueDate", "2026-06-25")
                .param("scheduleType", "one_time"))
                .andExpect(header().string("HX-Trigger", containsString("Name is required")))
        }
    }

    @Nested
    inner class ChoreDetail {

        @Test
        fun `returns detail with name due date and schedule`() {
            val id = repository.allChores.first().id
            mockMvc.perform(get("/chores/{id}/detail", id))
                .andExpect(status().isOk)
                .andExpect(content().string(containsString("Placeholder chore")))
                .andExpect(content().string(containsString("Due date")))
                .andExpect(content().string(containsString("Schedule")))
        }

        @Test
        fun `detail has edit and delete buttons`() {
            val id = repository.allChores.first().id
            mockMvc.perform(get("/chores/{id}/detail", id))
                .andExpect(status().isOk)
                .andExpect(content().string(containsString("Edit")))
                .andExpect(content().string(containsString("Delete")))
        }

        @Test
        fun `returns error message for non-existent chore`() {
            mockMvc.perform(get("/chores/{id}/detail", "00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isOk)
                .andExpect(content().string(containsString("Chore not found")))
        }
    }

    @Nested
    inner class CompleteChore {

        @Test
        fun `completes chore and returns fragment`() {
            val chore = repository.allChores.first()
            mockMvc.perform(post("/chores/{id}/complete", chore.id))
                .andExpect(status().isOk)
                .andExpect(content().string(not(containsString("Placeholder chore"))))
        }

        @Test
        fun `returns fragment without html wrapping after complete`() {
            val chore = repository.allChores.first()
            mockMvc.perform(post("/chores/{id}/complete", chore.id))
                .andExpect(content().string(not(containsString("<html"))))
        }
    }

    @Nested
    inner class DeleteChore {

        @Test
        fun `deletes chore and returns fragment`() {
            val chore = repository.allChores.first()
            mockMvc.perform(delete("/chores/{id}", chore.id))
                .andExpect(status().isOk)
                .andExpect(content().string(not(containsString("Placeholder chore"))))
        }

        @Test
        fun `returns fragment without html wrapping after delete`() {
            val chore = repository.allChores.first()
            mockMvc.perform(delete("/chores/{id}", chore.id))
                .andExpect(content().string(not(containsString("<html"))))
        }
    }
}
