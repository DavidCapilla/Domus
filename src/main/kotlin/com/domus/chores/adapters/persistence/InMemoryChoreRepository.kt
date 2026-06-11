package com.domus.chores.adapters.persistence

import com.domus.chores.core.Chore
import com.domus.chores.core.ChoreRepository
import org.springframework.stereotype.Repository

@Repository
class InMemoryChoreRepository() : ChoreRepository {

    private val chores: MutableSet<Chore> = HashSet()

    override fun findAll() = chores.toList()

    override fun findByName(name: String) = chores.find { it.name == name }

    override fun save(chore: Chore): Boolean {
        if (chores.any { it.name == chore.name }) return false
        return chores.add(chore)
    }

    override fun update(currentName: String, chore: Chore): Boolean {
        val existing = chores.find { it.name == currentName } ?: return false
        chores.remove(existing)
        chores.add(chore)
        return true
    }

    override fun delete(choreName: String) = chores.removeIf { it.name == choreName }
}
