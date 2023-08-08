package com.jdum.projector.nosql.redis

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class ProbabilisticExpirationCache(
        @Autowired
        val template: RedisTemplate<String, String>
) {
    val mapper: ObjectMapper = jacksonObjectMapper()
    private val timeToLive: Long = 5000 // in milliseconds
    private val beta: Double = 1.0

    data class CacheEntry(
        val value: String,
        val creationTime: Long,
        val expiryTime: Long
    )

    fun put(key: String, value: String) {
        val currentTime = System.currentTimeMillis()
        val expiryTime = currentTime + timeToLive
        val entry = mapper.writeValueAsString(CacheEntry(value, currentTime, expiryTime))
        template.opsForValue().set(key, entry)
    }

    fun get(key: String): CacheEntry? {
        val value = template.opsForValue().get(key) ?: return null
        val entry: CacheEntry = mapper.readValue(value)
        val currentTime = System.currentTimeMillis()
        val remainingTime = entry.expiryTime - currentTime
        val randomEarlyExpiration = remainingTime * beta * Random.nextDouble()
        if (randomEarlyExpiration <= 0) {
            println("Delete key: $key")
            template.delete(key)
            return null
        }
        return entry
    }
}
