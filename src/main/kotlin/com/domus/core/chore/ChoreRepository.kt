package com.domus.core.chore

interface ChoreRepository {
    fun findAll(): List<Chore>
    fun save(chore: Chore): Boolean
    fun delete(choreName: String): Boolean
}
