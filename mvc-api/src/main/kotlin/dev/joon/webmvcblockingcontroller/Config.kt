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
    private val connectionCounter = ConnectionCounter()
    private val logger = LoggerFactory.getLogger(Controller::class.java)

    @GetMapping("/ask")
    fun ask(): Map<String, String> {
        val start = Instant.now()
        val activeConnections = connectionCounter.increment()
        logger.info("ask: Request started at $start. Active connections: $activeConnections")

        repository.callDb() // DB 호출

        val end = Instant.now()
        val duration = java.time.Duration.between(start, end).toMillis()
        val currentConnections = connectionCounter.decrement() // 연결 수 감소
        logger.info("ask: Request ended at $end, took $duration ms. Active connections: $currentConnections")

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

class ConnectionCounter {
    val activeConnections = AtomicLong(0)
    val currentConnections: Long
        get() = activeConnections.get()

    fun increment(): Long {
        return activeConnections.incrementAndGet()
    }

    fun decrement(): Long {
        return activeConnections.decrementAndGet()
    }
}
