package com.domus.chores.core

import com.domus.chores.core.Chore

data class Dashboard(
    val overdue: List<Chore>,
    val dueToday: List<Chore>,
    val upcoming: List<Chore>,
)
