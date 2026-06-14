package com.domus.habits.adapters.web

import com.domus.habits.adapters.web.dto.HabitRequest
import com.domus.habits.adapters.web.dto.HabitResponse
import com.domus.habits.adapters.web.dto.toDomain
import com.domus.habits.application.CreateHabitUseCase
import com.domus.habits.application.DeleteHabitUseCase
import com.domus.habits.application.GetHabitProgressUseCase
import com.domus.habits.application.ListHabitsUseCase
import com.domus.habits.application.LogHabitCompletionUseCase
import com.domus.habits.application.UpdateHabitUseCase
import com.domus.habits.core.HabitAlreadyExistsException
import com.domus.habits.core.HabitNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/habits")
class HabitController(
    private val listHabitsUseCase: ListHabitsUseCase,
    private val createHabitUseCase: CreateHabitUseCase,
    private val updateHabitUseCase: UpdateHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val logHabitCompletionUseCase: LogHabitCompletionUseCase,
    private val getHabitProgressUseCase: GetHabitProgressUseCase,
) {

    @GetMapping
    fun getHabits(): List<HabitResponse> =
        getHabitProgressUseCase.getProgress().map { HabitResponse.fromDomain(it) }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addHabit(@RequestBody request: HabitRequest): HabitResponse {
        val habit = createHabitUseCase.addHabit(
            name = request.name,
            description = request.description,
            targetCount = request.targetCount,
            timeWindow = request.timeWindow.toDomain(),
        )
        return HabitResponse.fromDomain(habit)
    }

    @PutMapping("/{name}")
    fun updateHabit(
        @PathVariable name: String,
        @RequestBody request: HabitRequest,
    ): HabitResponse {
        val habit = updateHabitUseCase.updateHabit(
            currentName = name,
            newName = request.name,
            description = request.description,
            targetCount = request.targetCount,
            timeWindow = request.timeWindow.toDomain(),
        )
        return HabitResponse.fromDomain(habit)
    }

    @PostMapping("/{name}/log")
    fun logCompletion(@PathVariable name: String): HabitResponse {
        logHabitCompletionUseCase.logCompletion(name)
        return getHabitProgressUseCase.getProgress()
            .first { it.habit.name == name }
            .let { HabitResponse.fromDomain(it) }
    }

    @DeleteMapping("/{name}")
    fun deleteHabit(@PathVariable name: String) {
        deleteHabitUseCase.deleteHabit(name)
    }

    @ExceptionHandler(HabitAlreadyExistsException::class)
    fun handleAlreadyExists(): ResponseEntity<String> =
        ResponseEntity("Habit already exists", HttpStatus.CONFLICT)

    @ExceptionHandler(HabitNotFoundException::class)
    fun handleNotFound(): ResponseEntity<String> =
        ResponseEntity("Habit not found", HttpStatus.NOT_FOUND)
}
