package com.domus.chores.core

import java.util.UUID

interface ChoreRepository {
    fun findAll(): List<Chore>
    fun findByName(name: ChoreName): Chore?
    fun findById(id: UUID): Chore?
    fun save(chore: Chore): Boolean
    fun update(chore: Chore): Boolean
    fun delete(id: UUID): Boolean
}
