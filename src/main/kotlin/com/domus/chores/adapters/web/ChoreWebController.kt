package com.domus.chores.adapters.web

import com.domus.chores.adapters.web.dto.ChoreResponse
import com.domus.chores.adapters.web.dto.DashboardResponse
import com.domus.chores.application.CompleteChoreUseCase
import com.domus.chores.application.CreateChoreUseCase
import com.domus.chores.application.DeleteChoreUseCase
import com.domus.chores.application.GetChoreUseCase
import com.domus.chores.application.UpdateChoreUseCase
import com.domus.chores.application.GetDashboardUseCase
import com.domus.chores.core.ChoreAlreadyExistsException
import com.domus.chores.core.ChoreNotFoundException
import jakarta.servlet.http.HttpServletResponse
import java.time.LocalDate
import java.util.UUID
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PutMapping

@Controller
class ChoreWebController(
    private val getChoreUseCase: GetChoreUseCase,
    private val getDashboardUseCase: GetDashboardUseCase,
    private val createChoreUseCase: CreateChoreUseCase,
    private val updateChoreUseCase: UpdateChoreUseCase,
    private val completeChoreUseCase: CompleteChoreUseCase,
    private val deleteChoreUseCase: DeleteChoreUseCase,
) {
    @ModelAttribute("today")
    fun today(): LocalDate = LocalDate.now()

    @ModelAttribute("dashboard")
    fun dashboard(): DashboardResponse =
        DashboardResponse.fromDomain(getDashboardUseCase.getDashboard())

    @GetMapping("/")
    fun index(): String = "dashboard/page"

    @GetMapping("/chores")
    fun getChores(): String = "dashboard/list :: chore-list"

    @PostMapping("/chores")
    fun addChore(
        @ModelAttribute form: ChoreForm,
        model: Model,
        response: HttpServletResponse,
    ): String {
        if (form.name.isBlank()) {
            toast(response, "Name is required")
            return "dashboard/list :: chore-list"
        }
        val schedule = try {
            form.toSchedule()
        } catch (e: IllegalArgumentException) {
            toast(response, e.message ?: "Invalid schedule")
            return "dashboard/list :: chore-list"
        }
        try {
            createChoreUseCase.addChore(name = form.name, dueDate = form.dueDate, schedule = schedule)
        } catch (e: ChoreAlreadyExistsException) {
            toast(response, "A chore with this name already exists")
            model.addAttribute("dashboard", dashboard())
            return "dashboard/list :: chore-list"
        }
        model.addAttribute("dashboard", dashboard())
        return "dashboard/list :: chore-list"
    }

    @GetMapping("/chores/{id}/edit")
    fun editChoreForm(@PathVariable id: UUID, model: Model): String {
        val chore = getChoreUseCase.getChore(id)
        if (chore == null) {
            model.addAttribute("error", "Chore not found")
            return "fragments/error :: error-message"
        }
        model.addAttribute("chore", ChoreResponse.fromDomain(chore))
        return "dashboard/edit-chore :: chore-edit"
    }

    @GetMapping("/chores/{id}/detail")
    fun choreDetail(@PathVariable id: UUID, model: Model): String {
        val chore = getChoreUseCase.getChore(id)
        if (chore == null) {
            model.addAttribute("error", "Chore not found")
            return "fragments/error :: error-message"
        }
        model.addAttribute("chore", ChoreResponse.fromDomain(chore))
        return "dashboard/detail-chore :: chore-detail"
    }

    @PutMapping("/chores/{id}")
    fun updateChore(
        @PathVariable id: UUID,
        @ModelAttribute form: ChoreUpdateForm,
        model: Model,
        response: HttpServletResponse,
    ): String {
        if (form.name.isBlank()) {
            toast(response, "Name is required")
            return "dashboard/list :: chore-list"
        }
        val schedule = try {
            form.toSchedule()
        } catch (e: IllegalArgumentException) {
            toast(response, e.message ?: "Invalid schedule")
            return "dashboard/list :: chore-list"
        }
        try {
            updateChoreUseCase.updateChore(
                id = id,
                name = form.name,
                dueDate = form.dueDate,
                schedule = schedule
            )
        } catch (e: ChoreAlreadyExistsException) {
            toast(response, "A chore with this name already exists")
            model.addAttribute("dashboard", dashboard())
            return "dashboard/list :: chore-list"
        } catch (e: ChoreNotFoundException) {
            toast(response, "Chore not found.")
            model.addAttribute("dashboard", dashboard())
            return "dashboard/list :: chore-list"
        }
        model.addAttribute("dashboard", dashboard())
        return "dashboard/list :: chore-list"
    }

    @PostMapping("/chores/{id}/complete")
    fun completeChore(@PathVariable id: UUID, model: Model, response: HttpServletResponse): String {
        try {
            completeChoreUseCase.completeChore(id)
        } catch (e: ChoreNotFoundException) {
            toast(response, "Chore not found.")
            model.addAttribute("dashboard", dashboard())
            return "dashboard/list :: chore-list"
        }
        model.addAttribute("dashboard", dashboard())
        return "dashboard/list :: chore-list"
    }

    @DeleteMapping("/chores/{id}")
    fun deleteChore(@PathVariable id: UUID, model: Model, response: HttpServletResponse): String {
        try {
            deleteChoreUseCase.deleteChore(id)
        } catch (e: ChoreNotFoundException) {
            toast(response, "Chore not found.")
            model.addAttribute("dashboard", dashboard())
            return "dashboard/list :: chore-list"
        }
        model.addAttribute("dashboard", dashboard())
        return "dashboard/list :: chore-list"
    }

    private fun toast(response: HttpServletResponse, message: String) {
        response.addHeader("HX-Trigger", """{"showToast":"$message"}""")
    }
}
