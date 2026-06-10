package com.domus.adapters.web

import com.domus.createChore
import com.domus.core.chore.Chore
import com.domus.core.chore.ChoreRepository

class FakeChoreRepository : ChoreRepository {

    private val chores = mutableListOf<Chore>()

    fun reset() {
        chores.clear()
        chores.add(createChore(name = "Placeholder chore"))
    }

    override fun findAll(): List<Chore> = chores.toList()

    override fun findByName(name: String) = chores.find { it.name == name }

    override fun save(chore: Chore): Boolean {
        val alreadyExists = chores.any { it.name == chore.name }
        if (!alreadyExists) chores.add(chore)
        return !alreadyExists
    }

    override fun update(currentName: String, chore: Chore): Boolean {
        val existing = chores.find { it.name == currentName } ?: return false
        chores.remove(existing)
        chores.add(chore)
        return true
    }

    override fun delete(choreName: String): Boolean {
        val existing = chores.find { it.name == choreName } ?: return false
        return chores.remove(existing)
    }
}
