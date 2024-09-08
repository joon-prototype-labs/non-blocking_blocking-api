package dev.joon.webfluxblocking

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class Controller(
    private val repository: Repository,
) {
    @GetMapping("/ask")
    fun ask(): Mono<Map<String, String>> {
        return Mono.fromCallable {
            repository.callDb()
            mapOf("message" to "HI!")
        }
    }
}

interface Repository : JpaRepository<MyEntity, Long> {
    @Query(nativeQuery = true, value = "SELECT pg_sleep(1)")
    @Transactional(readOnly = true)
    fun callDb()
}

@Entity
class MyEntity(
    @Id
    var id: Long
)
