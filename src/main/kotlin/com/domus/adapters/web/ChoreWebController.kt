package com.domus.adapters.web

import com.domus.application.chore.CreateChoreUseCase
import com.domus.application.chore.DeleteChoreUseCase
import com.domus.application.chore.ListChoresUseCase
import com.domus.application.chore.UpdateChoreUseCase
import com.domus.core.chore.ChoreAlreadyExistsException
import com.domus.core.chore.ChoreNotFoundException
import com.domus.core.chore.Schedule
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import java.time.LocalDate
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class ChoreWebController(
    private val listChoresUseCase: ListChoresUseCase,
    private val createChoreUseCase: CreateChoreUseCase,
    private val updateChoreUseCase: UpdateChoreUseCase,
    private val deleteChoreUseCase: DeleteChoreUseCase,
) {

    @GetMapping("/")
    fun index(model: Model): String {
        model.addAttribute("chores", listChoresUseCase.getChores())
        return "index"
    }

    @GetMapping("/chores")
    fun getChores(model: Model): String {
        model.addAttribute("chores", listChoresUseCase.getChores())
        return "index :: chore-list"
    }

    @PostMapping("/chores")
    fun addChore(
        @RequestParam name: String,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) dueDate: LocalDate,
        @RequestParam scheduleType: String,
        @RequestParam(required = false) days: String?,
        model: Model,
    ): String {
        if (name.isBlank()) {
            model.addAttribute("error", "Name is required")
            model.addAttribute("chores", listChoresUseCase.getChores())
            return "index :: chore-list"
        }
        val schedule = when (scheduleType) {
            "one_time" -> Schedule.OneTime
            "every_n_days" -> {
                val daysInt = days?.toIntOrNull()
                if (daysInt == null || daysInt <= 0) {
                    model.addAttribute("error", "Days must be a positive number")
                    model.addAttribute("chores", listChoresUseCase.getChores())
                    return "index :: chore-list"
                }
                Schedule.EveryNDays(daysInt)
            }

            else -> throw IllegalArgumentException("Unknown schedule type: $scheduleType")
        }
        createChoreUseCase.addChore(name = name, dueDate = dueDate, schedule = schedule)
        model.addAttribute("chores", listChoresUseCase.getChores())
        return "index :: chore-list"
    }

    @GetMapping("/chores/{name}/edit")
    fun editChoreForm(@PathVariable name: String, model: Model): String {
        val chore = listChoresUseCase.getChores().find { it.name == name }
            ?: throw ChoreNotFoundException(name)
        model.addAttribute("chore", chore)
        return "index :: chore-edit"
    }

    @PutMapping("/chores/{name}")
    fun updateChore(
        @PathVariable name: String,
        @RequestParam newName: String,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) dueDate: LocalDate,
        @RequestParam scheduleType: String,
        @RequestParam(required = false) days: String?,
        model: Model,
    ): String {
        if (newName.isBlank()) {
            model.addAttribute("error", "Name is required")
            model.addAttribute("chores", listChoresUseCase.getChores())
            return "index :: chore-list"
        }
        val schedule = when (scheduleType) {
            "one_time" -> Schedule.OneTime
            "every_n_days" -> {
                val daysInt = days?.toIntOrNull()
                if (daysInt == null || daysInt <= 0) {
                    model.addAttribute("error", "Days must be a positive number")
                    model.addAttribute("chores", listChoresUseCase.getChores())
                    return "index :: chore-list"
                }
                Schedule.EveryNDays(daysInt)
            }

            else -> throw IllegalArgumentException("Unknown schedule type: $scheduleType")
        }
        updateChoreUseCase.updateChore(
            currentName = name,
            newName = newName,
            dueDate = dueDate,
            schedule = schedule
        )
        model.addAttribute("chores", listChoresUseCase.getChores())
        return "index :: chore-list"
    }

    @DeleteMapping("/chores/{name}")
    fun deleteChore(@PathVariable name: String, model: Model): String {
        deleteChoreUseCase.deleteChore(name)
        model.addAttribute("chores", listChoresUseCase.getChores())
        return "index :: chore-list"
    }

    @ExceptionHandler(ChoreAlreadyExistsException::class)
    fun handleChoreAlreadyExists(): ResponseEntity<String> =
        ResponseEntity("Chore already exists", HttpStatus.CONFLICT)

    @ExceptionHandler(ChoreNotFoundException::class)
    fun handleChoreNotFound(): ResponseEntity<String> =
        ResponseEntity("Chore not found", HttpStatus.NOT_FOUND)
}
