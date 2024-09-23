package dev.joon.webfluxnonblocking

import org.slf4j.LoggerFactory
import org.springframework.data.annotation.Id
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.concurrent.atomic.AtomicLong

private val logger = LoggerFactory.getLogger(ModuleLayer.Controller::class.java)
private val connectionCounter = ConnectionCounter()

@RestController
class Controller(
    private val repository: Repository,
) {
    @GetMapping("/ask")
    fun ask(): Mono<Map<String, String>> {
        return run(repository)
            .then(Mono.just(mapOf("message" to "HI!")))
    }
}

fun run(repository: Repository): Mono<Int> {
    val start = Instant.now()
    val activeConnections = connectionCounter.increment() // 연결 수 증가
    logger.info("ask: Request started at $start. Active connections: $activeConnections")

    val rs = repository.callDb() // DB 호출

    val end = Instant.now()
    val duration = java.time.Duration.between(start, end).toMillis()
    val currentConnections = connectionCounter.decrement() // 연결 수 감소
    logger.info("ask: Request ended at $end, took $duration ms. Active connections: $currentConnections")

    return rs
}

interface Repository : ReactiveCrudRepository<MyEntity, Long> {
    @Query("SELECT 1 as result FROM (SELECT pg_sleep(1)) as t")
    fun callDb(): Mono<Int>
}

@Table
class MyEntity(
    @Id var id: Long
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
