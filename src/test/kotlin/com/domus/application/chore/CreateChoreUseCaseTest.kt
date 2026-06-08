package com.domus.application.chore

import com.domus.core.chore.ChoreAlreadyExistsException
import com.domus.core.chore.ChoreRepository
import com.domus.core.chore.Schedule
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate

class CreateChoreUseCaseTest {

    private val repository = mock<ChoreRepository>()
    private val useCase = CreateChoreUseCase(repository)

    @Test
    fun `addChore saves chore to repository when new`() {
        whenever(repository.save(any())).doReturn(true)
        useCase.addChore("Take out trash", LocalDate.now(), Schedule.OneTime)
        verify(repository).save(any())
    }

    @Test
    fun `addChore throws when chore already exists`() {
        whenever(repository.save(any())).doReturn(false)
        assertThrows(ChoreAlreadyExistsException::class.java) {
            useCase.addChore("Clean kitchen", LocalDate.now(), Schedule.OneTime)
        }
    }
}