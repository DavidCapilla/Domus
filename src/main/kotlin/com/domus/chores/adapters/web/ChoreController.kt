package com.domus.chores.adapters.web

import com.domus.chores.adapters.web.dto.ChoreRequest
import com.domus.chores.adapters.web.dto.ChoreResponse
import com.domus.chores.adapters.web.dto.toDomain
import com.domus.chores.application.CompleteChoreUseCase
import com.domus.chores.application.CreateChoreUseCase
import com.domus.chores.application.DeleteChoreUseCase
import com.domus.chores.application.ListChoresUseCase
import com.domus.chores.application.UpdateChoreUseCase
import com.domus.chores.core.ChoreAlreadyExistsException
import com.domus.chores.core.ChoreNotFoundException
import jakarta.validation.Valid
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
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/chores")
class ChoreController(
    private val listChoresUseCase: ListChoresUseCase,
    private val createChoreUseCase: CreateChoreUseCase,
    private val updateChoreUseCase: UpdateChoreUseCase,
    private val completeChoreUseCase: CompleteChoreUseCase,
    private val deleteChoreUseCase: DeleteChoreUseCase,
) {

    @GetMapping
    fun getChores(): List<ChoreResponse> {
        return listChoresUseCase.getChores().map(ChoreResponse::fromDomain)
    }

    @PostMapping
    fun addChore(@Valid @RequestBody request: ChoreRequest) {
        createChoreUseCase.addChore(
            name = request.name,
            dueDate = request.dueDate,
            schedule = request.schedule.toDomain(),
        )
    }

    @PostMapping("/{name}/complete")
    fun completeChore(@PathVariable name: String) {
        completeChoreUseCase.completeChore(name)
    }

    @PutMapping("/{name}")
    fun updateChore(@PathVariable name: String, @Valid @RequestBody request: ChoreRequest): ChoreResponse {
        return ChoreResponse.fromDomain(
            updateChoreUseCase.updateChore(
                currentName = name,
                newName = request.name,
                dueDate = request.dueDate,
                schedule = request.schedule.toDomain(),
            )
        )
    }

    @DeleteMapping("/{name}")
    fun deleteChore(@PathVariable name: String) {
        deleteChoreUseCase.deleteChore(name)
    }

    @ExceptionHandler(ChoreAlreadyExistsException::class)
    fun handleChoreAlreadyExists(): ResponseEntity<String> =
        ResponseEntity("Chore already exists", HttpStatus.CONFLICT)

    @ExceptionHandler(ChoreNotFoundException::class)
    fun handleChoreNotFound(): ResponseEntity<String> =
        ResponseEntity("Chore not found", HttpStatus.NOT_FOUND)
}
