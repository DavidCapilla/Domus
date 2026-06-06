package com.domus.adapters.web

import com.domus.application.chore.ListChoresUseCase
import com.domus.core.chore.Chore
import com.domus.core.chore.ChoreAlreadyExistsException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class ChoreController(private val listChoresUseCase: ListChoresUseCase) {

    @GetMapping("/chores")
    fun getChores(): List<Chore> {
        return listChoresUseCase.getChores()
    }

    @PostMapping("/chore")
    fun addChore(@RequestBody chore: Chore) {
        listChoresUseCase.addChore(chore)
    }

    @ExceptionHandler(ChoreAlreadyExistsException::class)
    fun handleChoreAlreadyExists(): ResponseEntity<String> =
        ResponseEntity("Chore already exists", HttpStatus.CONFLICT)
}
