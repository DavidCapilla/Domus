package com.domus.chores.application

import com.domus.chores.core.ChoreAlreadyExistsException
import com.domus.chores.core.ChoreNotFoundException
import com.domus.chores.core.ChoreRepository
import com.domus.chores.core.Schedule
import com.domus.chores.createChore
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate

class UpdateChoreUseCaseTest {

    private val repository = mock<ChoreRepository>()
    private val useCase = UpdateChoreUseCase(repository)

    @Test
    fun `updateChore updates existing chore with new fields`() {
        val existing = createChore(name = "Clean kitchen", dueDate = LocalDate.now().plusDays(1))
        whenever(repository.findByName("Clean kitchen")).doReturn(existing)
        whenever(repository.update(any(), any())).doReturn(true)

        val updated = useCase.updateChore(
            currentName = "Clean kitchen",
            newName = "Clean kitchen (updated)",
            dueDate = LocalDate.now().plusDays(5),
            schedule = Schedule.EveryNDays(3),
        )

        assertEquals("Clean kitchen (updated)", updated.name)
        assertEquals(existing.id, updated.id)
        verify(repository).update("Clean kitchen", updated)
    }

    @Test
    fun `updateChore throws when current name not found`() {
        whenever(repository.findByName("Non-existent")).doReturn(null)

        assertThrows(ChoreNotFoundException::class.java) {
            useCase.updateChore(
                currentName = "Non-existent",
                newName = "Anything",
                dueDate = LocalDate.now(),
                schedule = Schedule.OneTime,
            )
        }
    }

    @Test
    fun `updateChore throws when new name conflicts with existing chore`() {
        val existing = createChore(name = "Clean kitchen")
        whenever(repository.findByName("Clean kitchen")).doReturn(existing)
        whenever(repository.findByName("Do laundry")).doReturn(createChore(name = "Do laundry"))

        assertThrows(ChoreAlreadyExistsException::class.java) {
            useCase.updateChore(
                currentName = "Clean kitchen",
                newName = "Do laundry",
                dueDate = LocalDate.now(),
                schedule = Schedule.OneTime,
            )
        }
    }

    @Test
    fun `updateChore preserves same name without conflict`() {
        val existing = createChore(name = "Clean kitchen", dueDate = LocalDate.now().plusDays(1))
        whenever(repository.findByName("Clean kitchen")).doReturn(existing)
        whenever(repository.update(any(), any())).doReturn(true)

        val updated = useCase.updateChore(
            currentName = "Clean kitchen",
            newName = "Clean kitchen",
            dueDate = LocalDate.now().plusDays(10),
            schedule = Schedule.OneTime,
        )

        assertEquals("Clean kitchen", updated.name)
        assertEquals(existing.id, updated.id)
    }
}
