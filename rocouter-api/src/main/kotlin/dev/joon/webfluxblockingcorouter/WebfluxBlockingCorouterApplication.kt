package dev.joon.webfluxblockingcorouter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebfluxBlockingCorouterApplication

fun main(args: Array<String>) {
    runApplication<WebfluxBlockingCorouterApplication>(*args)
}
