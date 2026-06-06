package com.domus

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DomusApplication

fun main(args: Array<String>) {
    runApplication<DomusApplication>(*args)
}
