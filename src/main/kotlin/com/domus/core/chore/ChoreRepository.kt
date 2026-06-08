package com.domus.core.chore

interface ChoreRepository {
    fun findAll(): List<Chore>
    fun findByName(name: String): Chore?
    fun save(chore: Chore): Boolean
    fun update(currentName: String, chore: Chore): Boolean
    fun delete(choreName: String): Boolean
}
