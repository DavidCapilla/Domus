package com.domus.chores.adapters.persistence

import com.domus.chores.core.Chore
import com.domus.chores.core.ChoreName
import com.domus.chores.core.ChoreRepository
import com.domus.chores.core.Schedule
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate
import java.util.UUID

@Repository
class CsvChoreRepository(
    @param:Value("\${domus.persistence.csv.path}") private val csvPath: Path,
) : ChoreRepository {

    private fun readAll(): List<Chore> {
        if (!Files.exists(csvPath)) return emptyList()
        return Files.readAllLines(csvPath).drop(1)
            .filter { it.isNotBlank() }
            .map { line ->
                val parts = line.split(",", limit = 5)
                Chore(
                    id = UUID.fromString(parts[0]),
                    name = ChoreName.of(parts[1]),
                    dueDate = LocalDate.parse(parts[2]),
                    schedule = when (parts[3]) {
                        "one_time" -> Schedule.OneTime
                        "every_n_days" -> Schedule.EveryNDays(parts[4].toInt())
                        else -> error("Unknown schedule type: ${parts[3]}")
                    },
                )
            }
    }

    private fun writeAll(chores: List<Chore>) {
        val dir = csvPath.parent
        if (dir != null && !Files.exists(dir)) Files.createDirectories(dir)
        val lines = mutableListOf("id,name,dueDate,scheduleType,days")
        lines.addAll(chores.map { it.toCsvLine() })
        Files.write(csvPath, lines)
    }

    private fun Chore.toCsvLine(): String {
        val (scheduleType, days) = when (val s = schedule) {
            is Schedule.OneTime -> "one_time" to ""
            is Schedule.EveryNDays -> "every_n_days" to s.days.toString()
        }
        return listOf(id.toString(), name.value, dueDate.toString(), scheduleType, days).joinToString(",")
    }

    @Synchronized
    override fun findAll(): List<Chore> = readAll()

    @Synchronized
    override fun findByName(name: ChoreName): Chore? = readAll().find { it.name == name }

    @Synchronized
    override fun findById(id: UUID): Chore? = readAll().find { it.id == id }

    @Synchronized
    override fun save(chore: Chore): Boolean {
        val chores = readAll().toMutableList()
        if (chores.any { it.name == chore.name }) return false
        chores.add(chore)
        writeAll(chores)
        return true
    }

    @Synchronized
    override fun update(id: UUID, chore: Chore): Boolean {
        val chores = readAll().toMutableList()
        val index = chores.indexOfFirst { it.id == id }
        if (index == -1) return false
        chores[index] = chore
        writeAll(chores)
        return true
    }

    @Synchronized
    override fun delete(id: UUID): Boolean {
        val chores = readAll().toMutableList()
        val removed = chores.removeAll { it.id == id }
        if (removed) writeAll(chores)
        return removed
    }
}
