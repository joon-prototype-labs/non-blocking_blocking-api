package dev.joon.webfluxcorouterblocking

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Bean
fun apiRouter(
    repository: Repository
) = coRouter {
    accept(MediaType.APPLICATION_JSON).nest {
        GET("/ask") {
            ServerResponse.ok().bodyValueAndAwait {
                repository.callDb()
                mapOf("message" to "HI!")
            }
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
