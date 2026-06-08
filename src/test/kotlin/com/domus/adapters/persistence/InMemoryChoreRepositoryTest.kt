package com.domus.adapters.persistence

import com.domus.core.chore.ChoreRepositoryContract

class InMemoryChoreRepositoryTest : ChoreRepositoryContract() {

    override val repository = InMemoryChoreRepository()
}
