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
        sleep(500)
        return mapOf("message" to "HI!")
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
