package com.domus.habits.core

import java.util.UUID

data class Habit(
    val id: UUID,
    val name: String,
    val description: String? = null,
    val targetCount: Int,
    val timeWindow: TimeWindow,
)
