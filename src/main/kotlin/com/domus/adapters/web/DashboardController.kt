package com.domus.adapters.web

import com.domus.adapters.web.dto.DashboardResponse
import com.domus.application.dashboard.GetDashboardUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/dashboard")
class DashboardController(
    private val getDashboardUseCase: GetDashboardUseCase,
) {

    @GetMapping
    fun getDashboard(): DashboardResponse {
        val dashboard = getDashboardUseCase.getDashboard()
        return DashboardResponse.fromDomain(dashboard)
    }
}
