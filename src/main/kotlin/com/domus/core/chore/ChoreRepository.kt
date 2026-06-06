package com.domus.core.chore

interface ChoreRepository {
    fun findAll(): List<Chore>
}