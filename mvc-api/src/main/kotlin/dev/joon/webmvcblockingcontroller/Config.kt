package dev.joon.webmvcblockingcontroller

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.slf4j.LoggerFactory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@RestController
@RequestMapping("/api")
class MvcController(private val repository: DummyRepository) {
    @GetMapping("/process")
    fun processData(): Map<String, Any> {
        val dataList = repository.getAllData()

        val processed = mutableListOf<String>()
        for (data in dataList) {
            if (data.length > 5) {
                processed.add(data.uppercase())
            }
        }

        val result = processed.take(10)
        val count = result.size

        return mapOf(
            "data" to result,
            "count" to count,
            "firstItem" to (result.firstOrNull() ?: "EMPTY")
        )
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
