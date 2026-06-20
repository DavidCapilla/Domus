package com.domus.chores.application

import com.domus.chores.core.ChoreAlreadyExistsException
import com.domus.chores.core.ChoreName
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
import java.util.UUID

class UpdateChoreUseCaseTest {

    private val repository = mock<ChoreRepository>()
    private val useCase = UpdateChoreUseCase(repository)

    @Test
    fun `updateChore updates existing chore with new fields`() {
        val existing = createChore(name = "Clean kitchen", dueDate = LocalDate.now().plusDays(1))
        whenever(repository.findById(existing.id)).doReturn(existing)
        whenever(repository.findByName(ChoreName.of("Clean kitchen (updated)"))).doReturn(null)
        whenever(repository.update(any(), any())).doReturn(true)

        val updated = useCase.updateChore(
            id = existing.id,
            name = "Clean kitchen (updated)",
            dueDate = LocalDate.now().plusDays(5),
            schedule = Schedule.EveryNDays(3),
        )

        assertEquals(ChoreName.of("Clean kitchen (updated)"), updated.name)
        assertEquals(existing.id, updated.id)
        verify(repository).update(existing.id, updated)
    }

    @Test
    fun `updateChore throws when id not found`() {
        val id = UUID.randomUUID()
        whenever(repository.findById(id)).doReturn(null)

        assertThrows(ChoreNotFoundException::class.java) {
            useCase.updateChore(
                id = id,
                name = "Anything",
                dueDate = LocalDate.now(),
                schedule = Schedule.OneTime,
            )
        }
    }

    @Test
    fun `updateChore throws when new name conflicts with existing chore`() {
        val existing = createChore(name = "Clean kitchen")
        val conflicting = createChore(name = "Do laundry")
        whenever(repository.findById(existing.id)).doReturn(existing)
        whenever(repository.findByName(ChoreName.of("Do laundry"))).doReturn(conflicting)

        assertThrows(ChoreAlreadyExistsException::class.java) {
            useCase.updateChore(
                id = existing.id,
                name = "Do laundry",
                dueDate = LocalDate.now(),
                schedule = Schedule.OneTime,
            )
        }
    }

    @Test
    fun `updateChore preserves same name without conflict`() {
        val existing = createChore(name = "Clean kitchen", dueDate = LocalDate.now().plusDays(1))
        whenever(repository.findById(existing.id)).doReturn(existing)
        whenever(repository.update(any(), any())).doReturn(true)

        val updated = useCase.updateChore(
            id = existing.id,
            name = "Clean kitchen",
            dueDate = LocalDate.now().plusDays(10),
            schedule = Schedule.OneTime,
        )

        assertEquals(ChoreName.of("Clean kitchen"), updated.name)
        assertEquals(existing.id, updated.id)
    }
}
