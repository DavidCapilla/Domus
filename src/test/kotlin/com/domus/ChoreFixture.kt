package com.domus

import com.domus.core.chore.Chore
import com.domus.core.chore.Schedule
import java.time.LocalDate
import java.util.UUID

fun createChore(
    id: UUID = UUID.randomUUID(),
    name: String,
    dueDate: LocalDate = LocalDate.now(),
    schedule: Schedule = Schedule.OneTime,
): Chore = Chore(id = id, name = name, dueDate = dueDate, schedule = schedule)
