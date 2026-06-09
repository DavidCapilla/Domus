package com.domus.core.dashboard

import com.domus.core.chore.Chore

data class Dashboard(
    val overdue: List<Chore>,
    val dueToday: List<Chore>,
    val upcoming: List<Chore>,
)
