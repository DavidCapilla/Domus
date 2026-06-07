package com.domus.adapters.web

import com.domus.application.chore.CreateChoreUseCase
import com.domus.application.chore.DeleteChoreUseCase
import com.domus.application.chore.ListChoresUseCase
import com.domus.core.chore.Chore
import com.domus.core.chore.ChoreAlreadyExistsException
import com.domus.core.chore.ChoreNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class ChoreController(
    private val listChoresUseCase: ListChoresUseCase,
    private val createChoreUseCase: CreateChoreUseCase,
    private val deleteChoreUseCase: DeleteChoreUseCase,
) {

    @GetMapping("/chores")
    fun getChores(): List<Chore> {
        return listChoresUseCase.getChores()
    }

    @PostMapping("/chore")
    fun addChore(@RequestBody chore: Chore) {
        createChoreUseCase.addChore(chore)
    }

    @DeleteMapping("/chore/{name}")
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
