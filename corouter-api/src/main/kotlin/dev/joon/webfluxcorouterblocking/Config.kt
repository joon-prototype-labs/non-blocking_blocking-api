package dev.joon.webfluxcorouterblocking

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter
import java.time.Instant
import java.util.concurrent.atomic.AtomicLong

@Configuration
class Config {

    private val logger = LoggerFactory.getLogger(ModuleLayer.Controller::class.java)
    private val connectionCounter = ConnectionCounter()

    @Bean
    fun apiRouter(
        repository: Repository
    ) = coRouter {
        GET("/ask") { request ->
            val start = Instant.now()
            val activeConnections = connectionCounter.increment() // 연결 수 증가
            logger.info("ask: Request started at $start. Active connections: $activeConnections")

            repository.callDb() // DB 호출

            val end = Instant.now()
            val duration = java.time.Duration.between(start, end).toMillis()
            val currentConnections = connectionCounter.decrement() // 연결 수 감소
            logger.info("ask: Request ended at $end, took $duration ms. Active connections: $currentConnections")

            val response = mapOf("message" to "HI!$")
            ServerResponse.ok().bodyValueAndAwait(response)
        }
        GET("/ask-without-db-call") { request ->
            val response = mapOf("message" to "HI!$")
            ServerResponse.ok().bodyValueAndAwait(response)
        }
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
