package com.domus.core.chore

import java.util.UUID

interface ChoreRepository {
    fun findAll(): List<Chore>
    fun findById(id: UUID): Chore?
    fun save(chore: Chore): Chore
    fun deleteById(id: UUID)
}