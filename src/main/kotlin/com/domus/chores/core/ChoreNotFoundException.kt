package com.domus.chores.core

class ChoreNotFoundException(name: String) : RuntimeException("Chore '$name' not found")
