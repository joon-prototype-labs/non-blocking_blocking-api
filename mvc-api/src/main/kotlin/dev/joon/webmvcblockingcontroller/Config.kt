package dev.joon.webmvcblockingcontroller

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller(
    private val repository: Repository,
) {
    @GetMapping("/ask")
    fun ask(): Map<String, String> {
        repository.callDb()
        val response = mapOf("message" to "HI!")
        return response
    }

    @GetMapping("/ask-without-db-call")
    fun askWithoutDbCall(): Map<String, String> {
        val response = mapOf("message" to "HI!")
        return response
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
