package dev.joon.webfluxnonblocking

import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.data.annotation.Id
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class Controller(
    private val repository: Repository,
) {
    @GetMapping("/ask")
    suspend fun ask(): Map<String, String> {
        repository.callDb().awaitFirst()
        return mapOf("message" to "HI!")
    }
}

interface Repository : ReactiveCrudRepository<MyEntity, Long> {
    @Query("SELECT pg_sleep(1)")
    fun callDb(): Mono<Void>
}

@Table
class MyEntity(
    @Id
    var id: Long
)
