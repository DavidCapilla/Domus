package com.domus.application.chore

import com.domus.createChore
import com.domus.core.chore.ChoreRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class ListChoresUseCaseTest {

    private val repository = mock<ChoreRepository> {
        on { findAll() } doReturn listOf(
            createChore(name = "Clean kitchen"),
            createChore(name = "Do laundry")
        )
    }
    private val useCase = ListChoresUseCase(repository)

    @Test
    fun `getChores returns chores from repository`() {
        val result = useCase.getChores()
        assertEquals(2, result.size)
        assertTrue(result.any { it.name == "Clean kitchen" })
        assertTrue(result.any { it.name == "Do laundry" })
    }

    @Test
    fun `getChores returns empty list when repository is empty`() {
        whenever(repository.findAll()) doReturn emptyList()
        val result = useCase.getChores()
        assertTrue(result.isEmpty())
    }
}
