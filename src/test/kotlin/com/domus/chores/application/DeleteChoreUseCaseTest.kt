package com.domus.chores.application

import com.domus.chores.core.ChoreNotFoundException
import com.domus.chores.core.ChoreRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class DeleteChoreUseCaseTest {

    private val repository = mock<ChoreRepository>()
    private val useCase = DeleteChoreUseCase(repository)

    @Test
    fun `deleteChore deletes existing chore`() {
        whenever(repository.delete("Clean kitchen")).doReturn(true)
        useCase.deleteChore("Clean kitchen")
        verify(repository).delete("Clean kitchen")
    }

    @Test
    fun `deleteChore throws when chore not found`() {
        whenever(repository.delete(any())).doReturn(false)
        assertThrows(ChoreNotFoundException::class.java) { useCase.deleteChore("Clean kitchen") }
    }
}