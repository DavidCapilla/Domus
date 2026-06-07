package com.domus

import com.domus.core.chore.Chore
import com.domus.core.chore.Schedule
import java.time.LocalDate

fun createChore(
    name: String,
    dueDate: LocalDate = LocalDate.now(),
    schedule: Schedule = Schedule.OneTime,
): Chore = Chore(name = name, dueDate = dueDate, schedule = schedule)
