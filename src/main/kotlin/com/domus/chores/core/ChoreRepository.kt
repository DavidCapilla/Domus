package com.domus.chores.core

interface ChoreRepository {
    fun findAll(): List<Chore>
    fun findByName(name: ChoreName): Chore?
    fun save(chore: Chore): Boolean
    fun update(currentName: ChoreName, chore: Chore): Boolean
    fun delete(choreName: ChoreName): Boolean
}
