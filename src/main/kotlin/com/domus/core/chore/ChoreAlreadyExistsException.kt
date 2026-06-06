package com.domus.core.chore

class ChoreAlreadyExistsException(name: String) : RuntimeException("Chore '$name' already exists")
