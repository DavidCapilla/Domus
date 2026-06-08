package com.domus.application.chore

import com.domus.core.chore.ChoreAlreadyExistsException
import com.domus.core.chore.ChoreRepository
import com.domus.core.chore.Schedule
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate

class CreateChoreUseCaseTest {

    private val repository = mock<ChoreRepository>()
    private val useCase = CreateChoreUseCase(repository)
    private val dueDate = LocalDate.now().plusDays(3)

    @Test
    fun `addChore saves chore with generated id and proper fields`() {
        whenever(repository.save(any())).doReturn(true)
        useCase.addChore("Take out trash", dueDate, Schedule.OneTime)
        verify(repository).save(argThat { chore ->
            (chore.name == "Take out trash"
                    && chore.dueDate.isEqual(dueDate)
                    && chore.schedule == Schedule.OneTime)
        })
    }

    @Test
    fun `addChore throws when chore already exists`() {
        whenever(repository.save(any())).doReturn(false)
        assertThrows(ChoreAlreadyExistsException::class.java) {
            useCase.addChore("Clean kitchen", dueDate, Schedule.OneTime)
        }
    }
}