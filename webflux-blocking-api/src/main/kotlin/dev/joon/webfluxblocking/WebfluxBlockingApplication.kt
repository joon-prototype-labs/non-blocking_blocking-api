package dev.joon.webfluxblocking

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebfluxBlockingApplication

fun main(args: Array<String>) {
    runApplication<WebfluxBlockingApplication>(*args)
}
