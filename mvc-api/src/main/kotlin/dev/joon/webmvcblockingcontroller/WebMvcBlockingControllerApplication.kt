package dev.joon.webmvcblockingcontroller

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebMvcBlockingControllerApplication

fun main(args: Array<String>) {
    runApplication<WebMvcBlockingControllerApplication>(*args)
}
