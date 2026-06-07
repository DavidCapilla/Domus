package com.domus.core.chore

class ChoreNotFoundException(name: String) : RuntimeException("Chore '$name' not found")
