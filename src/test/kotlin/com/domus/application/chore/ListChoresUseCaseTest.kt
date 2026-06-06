package com.domus.application.chore

import com.domus.core.chore.Chore
import com.domus.core.chore.ChoreAlreadyExistsException
import com.domus.core.chore.ChoreRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ListChoresUseCaseTest {

    private val repository = mock<ChoreRepository> {
        on { findAll() } doReturn listOf(Chore(name = "Clean kitchen"), Chore(name = "Do laundry"))
    }
    private val useCase = ListChoresUseCase(repository)

    @Test
    fun `getChores returns chores from repository`() {
        val expected = listOf(Chore(name = "Clean kitchen"), Chore(name = "Do laundry"))
        val result = useCase.getChores()
        assertEquals(expected, result)
    }

    @Test
    fun `getChores returns empty list when repository is empty`() {
        whenever(repository.findAll()) doReturn emptyList()
        val result = useCase.getChores()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `addChore saves chore to repository when new`() {
        whenever(repository.save(any())).doReturn(true)
        val chore = Chore(name = "Take out trash")
        useCase.addChore(chore)
        verify(repository).save(chore)
    }

    @Test
    fun `addChore throws when chore already exists`() {
        whenever(repository.save(any())).doReturn(false)
        val chore = Chore(name = "Clean kitchen")
        assertThrows(ChoreAlreadyExistsException::class.java) { useCase.addChore(chore) }
    }
}
