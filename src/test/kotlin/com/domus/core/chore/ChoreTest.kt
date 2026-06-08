package com.domus.core.chore

import com.domus.createChore
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ChoreTest {

    @Test
    fun `complete on one-time chore returns Finished`() {
        val chore = createChore(name = "Clean kitchen", schedule = Schedule.OneTime)
        val outcome = chore.complete(LocalDate.of(2026, 6, 8))
        assertEquals(CompletionOutcome.Finished, outcome)
    }

    @Test
    fun `complete on every-N-days chore returns Continued with next due date`() {
        val chore = createChore(name = "Water plants", schedule = Schedule.EveryNDays(3))
        val outcome = chore.complete(LocalDate.of(2026, 6, 8))
        val continued = outcome as CompletionOutcome.Continued
        assertEquals(LocalDate.of(2026, 6, 11), continued.chore.dueDate)
    }

    @Test
    fun `complete preserves id and name on rescheduled chore`() {
        val chore = createChore(name = "Water plants", schedule = Schedule.EveryNDays(3))
        val outcome = chore.complete(LocalDate.of(2026, 6, 8))
        val continued = outcome as CompletionOutcome.Continued
        assertEquals(chore.id, continued.chore.id)
        assertEquals(chore.name, continued.chore.name)
    }
}
