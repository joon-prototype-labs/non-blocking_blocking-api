package dev.joon.webfluxnonblocking

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebfluxNonBlockingApplication

fun main(args: Array<String>) {
    runApplication<WebfluxNonBlockingApplication>(*args)
}
