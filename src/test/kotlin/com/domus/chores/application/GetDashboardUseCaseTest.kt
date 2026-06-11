package com.domus.chores.application

import com.domus.chores.createChore
import com.domus.chores.core.ChoreRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneOffset

class GetDashboardUseCaseTest {

    private val fixedDate = LocalDate.of(2026, 6, 9)
    private val clock = Clock.fixed(
        fixedDate.atStartOfDay(ZoneOffset.UTC).toInstant(),
        ZoneOffset.UTC,
    )
    val repository = mock<ChoreRepository>()
    private val useCase = GetDashboardUseCase(repository, clock)

    private val overdueChore = createChore(name = "Overdue", dueDate = fixedDate.minusDays(1))
    private val dueTodayChore = createChore(name = "Due today", dueDate = fixedDate)
    private val upcomingChore = createChore(name = "Upcoming", dueDate = fixedDate.plusDays(1))

    @Test
    fun `returns empty dashboard when repository is empty`() {
        whenever(repository.findAll()).doReturn(emptyList())

        val dashboard = useCase.getDashboard()

        assertTrue(dashboard.overdue.isEmpty())
        assertTrue(dashboard.dueToday.isEmpty())
        assertTrue(dashboard.upcoming.isEmpty())
    }

    @Test
    fun `classifies overdue chore correctly`() {
        whenever(repository.findAll()).doReturn(listOf(overdueChore))

        val dashboard = useCase.getDashboard()

        assertEquals(1, dashboard.overdue.size)
        assertEquals("Overdue", dashboard.overdue[0].name)
        assertTrue(dashboard.dueToday.isEmpty())
        assertTrue(dashboard.upcoming.isEmpty())
    }

    @Test
    fun `classifies due today chore correctly`() {
        whenever(repository.findAll()).doReturn(listOf(dueTodayChore))

        val dashboard = useCase.getDashboard()

        assertEquals(1, dashboard.dueToday.size)
        assertEquals("Due today", dashboard.dueToday[0].name)
        assertTrue(dashboard.overdue.isEmpty())
        assertTrue(dashboard.upcoming.isEmpty())
    }

    @Test
    fun `classifies upcoming chore correctly`() {
        whenever(repository.findAll()).doReturn(listOf(upcomingChore))

        val dashboard = useCase.getDashboard()

        assertEquals(1, dashboard.upcoming.size)
        assertEquals("Upcoming", dashboard.upcoming[0].name)
        assertTrue(dashboard.overdue.isEmpty())
        assertTrue(dashboard.dueToday.isEmpty())
    }

    @Test
    fun `classifies mixed chores correctly`() {
        whenever(repository.findAll()).doReturn(listOf(overdueChore, dueTodayChore, upcomingChore))

        val dashboard = useCase.getDashboard()

        assertEquals(1, dashboard.overdue.size)
        assertEquals("Overdue", dashboard.overdue[0].name)
        assertEquals(1, dashboard.dueToday.size)
        assertEquals("Due today", dashboard.dueToday[0].name)
        assertEquals(1, dashboard.upcoming.size)
        assertEquals("Upcoming", dashboard.upcoming[0].name)
    }
}
