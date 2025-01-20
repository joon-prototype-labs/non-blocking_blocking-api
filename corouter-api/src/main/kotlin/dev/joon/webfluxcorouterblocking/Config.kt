package dev.joon.webfluxcorouterblocking

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Configuration
class CoroutineRouterConfig(private val repository: DummyRepository) {
    @Bean
    fun apiRouter() = coRouter {
        "/api".nest {
            GET("/process") {
                // MVC 스타일과 유사하게 직접적인 데이터 처리
                val dataList = repository.getAllData()

                val processed = mutableListOf<String>()
                for (data in dataList) {
                    if (data.length > 5) {
                        processed.add(data.uppercase())
                    }
                }

                val result = processed.take(10)
                val count = result.size

                val response = mapOf(
                    "data" to result,
                    "count" to count,
                    "firstItem" to (result.firstOrNull() ?: "EMPTY")
                )

                ServerResponse.ok().bodyValueAndAwait(response)
            }
        }
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
