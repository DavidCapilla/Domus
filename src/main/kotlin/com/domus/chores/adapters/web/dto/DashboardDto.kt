package com.domus.chores.adapters.web.dto

import com.domus.chores.core.Dashboard

data class DashboardResponse(
    val overdue: List<ChoreResponse>,
    val dueToday: List<ChoreResponse>,
    val upcoming: List<ChoreResponse>,
) {
    companion object {
        fun fromDomain(dashboard: Dashboard) = DashboardResponse(
            overdue = dashboard.overdue.map(ChoreResponse::fromDomain),
            dueToday = dashboard.dueToday.map(ChoreResponse::fromDomain),
            upcoming = dashboard.upcoming.map(ChoreResponse::fromDomain),
        )
    }
}
