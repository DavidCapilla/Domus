package com.domus.core.chore

import java.time.LocalDate

data class Chore(
    val name: String,
    val dueDate: LocalDate,
    val schedule: Schedule = Schedule.OneTime,
)
