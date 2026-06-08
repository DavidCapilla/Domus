package com.domus.application.chore

import com.domus.core.chore.ChoreNotFoundException
import com.domus.core.chore.ChoreRepository
import com.domus.core.chore.CompletionOutcome
import com.domus.core.chore.Schedule
import com.domus.createChore
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

class CompleteChoreUseCaseTest {

    private val repository = mock<ChoreRepository>()
    private val useCase = CompleteChoreUseCase(repository)

    @Test
    fun `completeChore deletes one-time chore`() {
        val chore = createChore(name = "Clean kitchen", schedule = Schedule.OneTime)
        whenever(repository.findByName("Clean kitchen")).doReturn(chore)
        whenever(repository.delete("Clean kitchen")).doReturn(true)

        val outcome = useCase.completeChore("Clean kitchen")

        assertEquals(CompletionOutcome.Finished, outcome)
        verify(repository).delete("Clean kitchen")
        verify(repository, never()).update(any(), any())
    }

    @Test
    fun `completeChore reschedules every-N-days chore`() {
        val chore = createChore(name = "Water plants", schedule = Schedule.EveryNDays(3))
        whenever(repository.findByName("Water plants")).doReturn(chore)
        whenever(repository.update(any(), any())).doReturn(true)

        val outcome = useCase.completeChore("Water plants")

        val continued = outcome as CompletionOutcome.Continued
        assertEquals(chore.id, continued.chore.id)
        assertEquals(LocalDate.now().plusDays(3), continued.chore.dueDate)
        verify(repository, never()).delete(any())
        verify(repository).update("Water plants", continued.chore)
    }

    @Test
    fun `completeChore throws when chore not found`() {
        whenever(repository.findByName("Non-existent")).doReturn(null)

        assertThrows(ChoreNotFoundException::class.java) {
            useCase.completeChore("Non-existent")
        }
    }
}
