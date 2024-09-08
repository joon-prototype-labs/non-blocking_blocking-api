package dev.joon.webfluxcorouterblocking

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebfluxBlockingCorouterApplication

fun main(args: Array<String>) {
    runApplication<WebfluxBlockingCorouterApplication>(*args)
}
