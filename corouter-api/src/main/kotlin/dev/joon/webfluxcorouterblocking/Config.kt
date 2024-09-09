package dev.joon.webfluxcorouterblocking

import jakarta.persistence.Entity
import jakarta.persistence.Id
import kotlinx.coroutines.delay
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Configuration
class Config {
    @Bean
    fun apiRouter(
        repository: Repository
    ) = coRouter {
        GET("/ask") { request ->
            repository.callDb()
            delay(500)
            val response = mapOf("message" to "HI!")
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
