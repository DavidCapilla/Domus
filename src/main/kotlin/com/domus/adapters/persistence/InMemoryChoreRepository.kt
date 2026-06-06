package com.domus.adapters.persistence

import com.domus.core.chore.Chore
import com.domus.core.chore.ChoreRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class InMemoryChoreRepository(private val chores: Map<UUID, Chore>) : ChoreRepository {

    override fun findAll(): List<Chore> = chores.values.toList()
}
