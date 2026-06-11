package com.domus.chores.core

interface ChoreRepository {
    fun findAll(): List<Chore>
    fun findByName(name: String): Chore?
    fun save(chore: Chore): Boolean
    fun update(currentName: String, chore: Chore): Boolean
    fun delete(choreName: String): Boolean
}
