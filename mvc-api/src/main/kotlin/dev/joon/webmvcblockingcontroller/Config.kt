package dev.joon.webmvcblockingcontroller

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.slf4j.LoggerFactory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.concurrent.atomic.AtomicLong

@RestController
class Controller(
    private val repository: Repository,
) {
    private val logger = LoggerFactory.getLogger(Controller::class.java)

    @GetMapping("/ask")
    fun ask(): Map<String, String> {
        val start = Instant.now()
        logger.info("ask: Request started at $start")

        repository.callDb()

        val end = Instant.now()
        val duration = java.time.Duration.between(start, end).toMillis()
        logger.info("ask: Request ended at $end, took $duration ms")
        val response = mapOf("message" to "HI!")
        return response
    }

    @GetMapping("/ask-without-db-call")
    fun askWithoutDbCall(): Map<String, String> {
        val response = mapOf("message" to "HI!")
        return response
    }
}

interface Repository : JpaRepository<MyEntity, Long> {
    @Query(nativeQuery = true, value = "select pg_sleep(1)")
    fun callDb(): Unit
}

@Entity
class MyEntity(
    @Id
    var id: Long
)
