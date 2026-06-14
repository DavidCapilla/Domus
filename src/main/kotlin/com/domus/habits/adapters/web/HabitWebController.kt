package com.domus.habits.adapters.web

import com.domus.habits.application.CreateHabitUseCase
import com.domus.habits.application.DeleteHabitUseCase
import com.domus.habits.application.GetHabitProgressUseCase
import com.domus.habits.application.ListHabitsUseCase
import com.domus.habits.application.LogHabitCompletionUseCase
import com.domus.habits.application.UpdateHabitUseCase
import com.domus.habits.core.HabitAlreadyExistsException
import com.domus.habits.core.HabitNotFoundException
import com.domus.habits.core.TimeWindow
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/habits")
class HabitWebController(
    private val listHabitsUseCase: ListHabitsUseCase,
    private val getHabitProgressUseCase: GetHabitProgressUseCase,
    private val createHabitUseCase: CreateHabitUseCase,
    private val updateHabitUseCase: UpdateHabitUseCase,
    private val logHabitCompletionUseCase: LogHabitCompletionUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
) {

    @GetMapping
    fun index(model: Model): String {
        model.addAttribute("progressList", getHabitProgressUseCase.getProgress())
        return "habits/page"
    }

    @GetMapping("/list")
    fun getHabits(model: Model): String {
        model.addAttribute("progressList", getHabitProgressUseCase.getProgress())
        return "habits/page :: habit-list"
    }

    @PostMapping
    fun addHabit(
        @RequestParam name: String,
        @RequestParam(required = false) description: String?,
        @RequestParam targetCount: Int,
        @RequestParam timeWindowType: String,
        @RequestParam(required = false) days: String?,
        model: Model,
    ): String {
        if (name.isBlank()) {
            model.addAttribute("error", "Name is required")
            model.addAttribute("progressList", getHabitProgressUseCase.getProgress())
            return "habits/page :: habit-list"
        }
        val timeWindow = when (timeWindowType) {
            "daily" -> TimeWindow.Daily
            "weekly" -> TimeWindow.Weekly
            "monthly" -> TimeWindow.Monthly
            "every_n_days" -> {
                val daysInt = days?.toIntOrNull()
                if (daysInt == null || daysInt <= 0) {
                    model.addAttribute("error", "Days must be a positive number")
                    model.addAttribute("progressList", getHabitProgressUseCase.getProgress())
                    return "habits/page :: habit-list"
                }
                TimeWindow.EveryNDays(daysInt)
            }
            else -> throw IllegalArgumentException("Unknown time window: $timeWindowType")
        }
        createHabitUseCase.addHabit(
            name = name,
            description = description?.takeIf { it.isNotBlank() },
            targetCount = targetCount,
            timeWindow = timeWindow,
        )
        model.addAttribute("progressList", getHabitProgressUseCase.getProgress())
        return "habits/page :: habit-list"
    }

    @GetMapping("/{name}/edit")
    fun editHabitForm(@PathVariable name: String, model: Model): String {
        val progress = getHabitProgressUseCase.getProgress().find { it.habit.name == name }
            ?: throw HabitNotFoundException(name)
        model.addAttribute("progress", progress)
        return "habits/page :: habit-edit"
    }

    @PutMapping("/{name}")
    fun updateHabit(
        @PathVariable name: String,
        @RequestParam newName: String,
        @RequestParam(required = false) description: String?,
        @RequestParam targetCount: Int,
        @RequestParam timeWindowType: String,
        @RequestParam(required = false) days: String?,
        model: Model,
    ): String {
        if (newName.isBlank()) {
            model.addAttribute("error", "Name is required")
            model.addAttribute("progressList", getHabitProgressUseCase.getProgress())
            return "habits/page :: habit-list"
        }
        val timeWindow = when (timeWindowType) {
            "daily" -> TimeWindow.Daily
            "weekly" -> TimeWindow.Weekly
            "monthly" -> TimeWindow.Monthly
            "every_n_days" -> {
                val daysInt = days?.toIntOrNull()
                if (daysInt == null || daysInt <= 0) {
                    model.addAttribute("error", "Days must be a positive number")
                    model.addAttribute("progressList", getHabitProgressUseCase.getProgress())
                    return "habits/page :: habit-list"
                }
                TimeWindow.EveryNDays(daysInt)
            }
            else -> throw IllegalArgumentException("Unknown time window: $timeWindowType")
        }
        updateHabitUseCase.updateHabit(
            currentName = name,
            newName = newName,
            description = description?.takeIf { it.isNotBlank() },
            targetCount = targetCount,
            timeWindow = timeWindow,
        )
        model.addAttribute("progressList", getHabitProgressUseCase.getProgress())
        return "habits/page :: habit-list"
    }

    @PostMapping("/{name}/log")
    fun logCompletion(@PathVariable name: String, model: Model): String {
        logHabitCompletionUseCase.logCompletion(name)
        model.addAttribute("progressList", getHabitProgressUseCase.getProgress())
        return "habits/page :: habit-list"
    }

    @DeleteMapping("/{name}")
    fun deleteHabit(@PathVariable name: String, model: Model): String {
        deleteHabitUseCase.deleteHabit(name)
        model.addAttribute("progressList", getHabitProgressUseCase.getProgress())
        return "habits/page :: habit-list"
    }

    @ExceptionHandler(HabitAlreadyExistsException::class)
    fun handleAlreadyExists(): ResponseEntity<String> =
        ResponseEntity("Habit already exists", HttpStatus.CONFLICT)

    @ExceptionHandler(HabitNotFoundException::class)
    fun handleNotFound(): ResponseEntity<String> =
        ResponseEntity("Habit not found", HttpStatus.NOT_FOUND)
}
