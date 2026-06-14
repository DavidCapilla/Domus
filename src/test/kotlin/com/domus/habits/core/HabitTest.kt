package com.domus.habits.core

import com.domus.habits.createHabit
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HabitTest {

    @Test
    fun `habit is created with given fields`() {
        val habit = createHabit(name = "Read", description = "30 min", targetCount = 3, timeWindow = TimeWindow.Weekly)
        assertEquals("Read", habit.name)
        assertEquals("30 min", habit.description)
        assertEquals(3, habit.targetCount)
        assertEquals(TimeWindow.Weekly, habit.timeWindow)
    }
}
