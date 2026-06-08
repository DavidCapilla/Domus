package com.domus.core.chore

import java.time.LocalDate
import java.util.UUID

data class Chore(
    val id: UUID,
    val name: String,
    val dueDate: LocalDate,
    val schedule: Schedule,
) {
    fun complete(on: LocalDate): CompletionOutcome = when (schedule) {
        is Schedule.OneTime -> CompletionOutcome.Finished
        is Schedule.EveryNDays -> CompletionOutcome.Continued(copy(dueDate = on.plusDays(schedule.days.toLong())))
    }
}
