package com.domus.core.chore

import java.time.LocalDate
import java.util.UUID

data class Chore(
    val id: UUID,
    val name: String,
    val dueDate: LocalDate,
    val schedule: Schedule,
)
