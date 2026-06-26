package com.domus.chores.adapters.web

import com.domus.chores.createChore
import com.domus.chores.core.Chore
import com.domus.chores.core.ChoreName
import com.domus.chores.core.ChoreRepository
import java.util.UUID

class FakeChoreRepository : ChoreRepository {

    private val chores = mutableListOf<Chore>()

    fun reset() {
        chores.clear()
        chores.add(createChore(name = "Placeholder chore"))
    }

    val allChores: List<Chore> get() = chores.toList()

    override fun findAll(): List<Chore> = chores.toList()

    override fun findByName(name: ChoreName) = chores.find { it.name == name }

    override fun findById(id: UUID) = chores.find { it.id == id }

    override fun save(chore: Chore): Boolean {
        val alreadyExists = chores.any { it.id == chore.id }
        if (!alreadyExists) chores.add(chore)
        return !alreadyExists
    }

    override fun update(chore: Chore): Boolean {
        val existing = chores.find { it.id == chore.id } ?: return false
        chores.remove(existing)
        chores.add(chore)
        return true
    }

    override fun delete(id: UUID): Boolean {
        val existing = chores.find { it.id == id } ?: return false
        return chores.remove(existing)
    }
}
