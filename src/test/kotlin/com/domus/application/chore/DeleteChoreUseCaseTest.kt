package com.domus.application.chore

import com.domus.core.chore.Chore
import com.domus.core.chore.ChoreNotFoundException
import com.domus.core.chore.ChoreRepository
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
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
        whenever(repository.delete(any())).doReturn(true)
        useCase.deleteChore("Clean kitchen")
        verify(repository).delete(Chore(name = "Clean kitchen"))
    }

    @Test
    fun `deleteChore throws when chore not found`() {
        whenever(repository.delete(any())).doReturn(false)
        assertThrows(ChoreNotFoundException::class.java) { useCase.deleteChore("Clean kitchen") }
    }
}