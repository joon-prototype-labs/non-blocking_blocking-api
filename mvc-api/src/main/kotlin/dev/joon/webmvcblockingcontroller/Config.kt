package dev.joon.webmvcblockingcontroller

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.lang.Thread.sleep

@RestController
class Controller(
    private val repository: Repository,
) {
    @GetMapping("/ask")
    fun ask(): Map<String, String> {
        repository.callDb()
        val startTime = System.currentTimeMillis()
        var i = 0L
        while (System.currentTimeMillis() - startTime < 500) {
            i += startTime
        }
        val response = mapOf("message" to "HI!$i")
        return response
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
