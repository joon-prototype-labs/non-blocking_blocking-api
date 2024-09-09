package dev.joon.webfluxnonblocking

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
    fun ask(): Mono<Map<String, String>> {
        return repository.callDb()
            .then(Mono.just(mapOf("message" to "HI!")))
    }
}

interface Repository : ReactiveCrudRepository<MyEntity, Long> {
    @Query("SELECT 1 as result FROM (SELECT pg_sleep(1)) as t")
    fun callDb(): Mono<Int>
}

@Table
class MyEntity(
    @Id var id: Long
)
