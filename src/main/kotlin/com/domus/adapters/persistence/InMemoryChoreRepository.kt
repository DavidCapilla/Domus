package com.domus.adapters.persistence

import com.domus.core.chore.Chore
import com.domus.core.chore.ChoreRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class InMemoryChoreRepository() : ChoreRepository {

    private val chores: MutableSet<Chore> = HashSet()

    override fun findByName(name: String) = chores.find { it.name == name }

    override fun findAll() = chores.toList()

    override fun save(chore: Chore) = chores.add(chore)

    override fun delete(chore: Chore) = chores.remove(chore)
}
