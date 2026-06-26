package com.domus.chores.application

import com.domus.chores.core.ChoreNotFoundException
import com.domus.chores.core.ChoreRepository
import com.domus.chores.core.CompletionOutcome
import com.domus.chores.core.Schedule
import com.domus.chores.createChore
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.util.UUID

class CompleteChoreUseCaseTest {

    private val repository = mock<ChoreRepository>()
    private val useCase = CompleteChoreUseCase(repository)

    @Test
    fun `completeChore deletes one-time chore`() {
        val chore = createChore(name = "Clean kitchen", schedule = Schedule.OneTime)
        whenever(repository.findById(chore.id)).doReturn(chore)
        whenever(repository.delete(chore.id)).doReturn(true)

        val outcome = useCase.completeChore(chore.id)

        assertEquals(CompletionOutcome.Finished, outcome)
        verify(repository).delete(chore.id)
        verify(repository, never()).update(any())
    }

    @Test
    fun `completeChore reschedules every-N-days chore`() {
        val chore = createChore(name = "Water plants", schedule = Schedule.EveryNDays(3))
        whenever(repository.findById(chore.id)).doReturn(chore)
        whenever(repository.update(any())).doReturn(true)

        val outcome = useCase.completeChore(chore.id)

        val continued = outcome as CompletionOutcome.Continued
        assertEquals(chore.id, continued.chore.id)
        assertEquals(LocalDate.now().plusDays(3), continued.chore.dueDate)
        verify(repository, never()).delete(any())
        verify(repository).update(continued.chore)
    }

    @Test
    fun `completeChore throws when chore not found`() {
        val id = UUID.randomUUID()
        whenever(repository.findById(id)).doReturn(null)

        assertThrows(ChoreNotFoundException::class.java) {
            useCase.completeChore(id)
        }
    }
}
