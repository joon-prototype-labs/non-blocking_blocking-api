package dev.joon.webfluxblocking

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap

@RestController
@RequestMapping("/api")
class ReactiveController(private val repository: DummyRepository) {
    @GetMapping("/process")
    fun processData(): Mono<Map<String, Any>> {
        return Flux.fromIterable(repository.getAllData())
            .filter { it.length > 5 }
            .map { it.uppercase() }
            .take(10)
            .collectList()
            .map { list ->
                mapOf(
                    "data" to list,
                    "count" to list.size,
                    "firstItem" to (list.firstOrNull() ?: "EMPTY")
                )
            }
    }

    @GetMapping("/multi")
    fun getMultipleData(): Mono<Map<String, List<String>>> {
        return Flux.fromIterable(listOf("1", "2", "3", "4", "5"))
            .map { repository.getData(it) }
            .collectList()
            .map { mapOf("items" to it) }
    }
}

@Repository
class DummyRepository {
    private val dataMap = ConcurrentHashMap<String, String>()

    init {
        repeat(1000) {
            dataMap[it.toString()] = "Data-$it"
        }
    }

    fun getAllData(): List<String> = dataMap.values.toList()
    fun getData(key: String): String = dataMap[key] ?: "NOT_FOUND"
}


@Entity
class MyEntity(
    @Id
    var id: Long
)
