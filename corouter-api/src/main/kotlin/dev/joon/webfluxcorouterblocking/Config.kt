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

@Configuration
class Config {

    private val logger = LoggerFactory.getLogger(ModuleLayer.Controller::class.java)

    @Bean
    fun apiRouter(
        repository: Repository
    ) = coRouter {
        GET("/ask") { request ->
            val start = Instant.now()
            logger.info("ask: Request started at $start")

            repository.callDb()

            val end = Instant.now()
            val duration = java.time.Duration.between(start, end).toMillis()
            logger.info("ask: Request ended at $end, took $duration ms")

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
    @Query(nativeQuery = true, value = "select pg_sleep(0.1)")
    fun callDb(): Unit
}

@Entity
class MyEntity(
    @Id
    var id: Long
)
