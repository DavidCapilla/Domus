package com.domus.chores

import com.domus.chores.core.Chore
import com.domus.chores.core.ChoreName
import com.domus.chores.core.Schedule
import java.time.LocalDate
import java.util.UUID

fun createChore(
    id: UUID = UUID.randomUUID(),
    name: String,
    dueDate: LocalDate = LocalDate.now(),
    schedule: Schedule = Schedule.OneTime,
): Chore = Chore(id = id, name = ChoreName.of(name), dueDate = dueDate, schedule = schedule)
