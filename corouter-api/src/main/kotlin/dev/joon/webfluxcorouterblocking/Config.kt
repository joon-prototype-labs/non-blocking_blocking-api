package dev.joon.webfluxcorouterblocking

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class Config {
    @Bean
    fun apiRouter(
        repository: Repository
    ) = coRouter {
        GET("/ask") { request ->
            repository.callDb()
            val startTime = System.currentTimeMillis()
            var i = 0L
            while (System.currentTimeMillis() - startTime < 500) {
                i += startTime
            }
            val response = mapOf("message" to "HI!$i")
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
