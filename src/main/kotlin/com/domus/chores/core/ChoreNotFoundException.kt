package com.domus.chores.core

import java.util.UUID

class ChoreNotFoundException(id: UUID) : RuntimeException("Chore '$id' not found")
