package com.domus.chores.core

import java.time.LocalDate
import java.util.UUID

data class Chore(
    val id: UUID,
    val name: ChoreName,
    val dueDate: LocalDate,
    val schedule: Schedule,
) {
    fun complete(on: LocalDate): CompletionOutcome = when (schedule) {
        is Schedule.OneTime -> CompletionOutcome.Finished
        is Schedule.EveryNDays -> CompletionOutcome.Continued(copy(dueDate = on.plusDays(schedule.days.toLong())))
    }
}

data class ChoreName private constructor(val value: String) {

    init {
        require(value.isNotBlank()) { "Name cannot be blank" }
    }

    companion object {
        fun of(raw: String): ChoreName =
            ChoreName(raw.trim())
    }

    override fun equals(other: Any?): Boolean =
        other is ChoreName && value.equals(other.value, ignoreCase = true)

    override fun hashCode(): Int = value.lowercase().hashCode()
}
