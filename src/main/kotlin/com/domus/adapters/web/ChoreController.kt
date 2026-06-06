package com.domus.adapters.web

import com.domus.application.chore.ListChoresUseCase
import com.domus.core.chore.Chore
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class ChoreController (private val listChoresUseCase: ListChoresUseCase) {

    @GetMapping("/chores")
    fun getChores(): List<Chore> {
        return listChoresUseCase.getChores()
    }
}
