package com.domus.core.chore

interface ChoreRepository {
    fun findByName(name: String): Chore?
    fun findAll(): List<Chore>
    fun save(chore: Chore): Boolean
    fun delete(chore: Chore): Boolean
}