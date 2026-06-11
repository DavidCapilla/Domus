package com.domus.chores.adapters.persistence

import com.domus.chores.core.ChoreRepositoryContract

class InMemoryChoreRepositoryTest : ChoreRepositoryContract() {

    override val repository = InMemoryChoreRepository()
}
