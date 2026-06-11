package com.domus.chores.core

class ChoreAlreadyExistsException(name: String) : RuntimeException("Chore '$name' already exists")
