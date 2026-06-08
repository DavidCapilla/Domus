package com.domus.adapters.persistence

import com.domus.core.chore.Chore
import com.domus.core.chore.ChoreRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class InMemoryChoreRepository() : ChoreRepository {

    private val chores: MutableSet<Chore> = HashSet()

    override fun findAll() = chores.toList()

    override fun save(chore: Chore): Boolean {
        if (chores.any { it.name == chore.name }) return false
        return chores.add(chore)
    }

    override fun delete(choreName: String) = chores.removeIf { it.name == choreName }
}
