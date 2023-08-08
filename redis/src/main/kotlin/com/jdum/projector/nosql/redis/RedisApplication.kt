package com.jdum.projector.nosql.redis

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.redis.core.RedisTemplate
import java.util.*
import java.util.concurrent.TimeUnit

@SpringBootApplication
class RedisApplication(

        @Autowired
        val template: RedisTemplate<String, String>,
        @Autowired
        val cache: ProbabilisticExpirationCache

) : CommandLineRunner {

    override fun run(vararg args: String?) {
//        volatilePolicies()
//        allKeysLru()
//        allKeysLfu()
//        allKeysRandom()
//        probabilisticCaching()
    }

    fun probabilisticCaching() {
        cache.put("key-1", "value-1")
        repeat(10) {
            Thread.sleep(1000)
            println("Read value $it: ${cache.get("key-1")}")
        }
    }

    fun get(key: String) {
        if (template.opsForValue().get(key) != null) println("Found: $key")
        else println("evicted $key")
    }

    fun allKeysLru() {
        template.opsForValue().set("constant-key-1", UUID.randomUUID().toString())
        template.opsForValue().set("constant-key-2", UUID.randomUUID().toString())
        template.opsForValue().set("constant-key-3", UUID.randomUUID().toString())

        (1..500).toList().forEach { i ->
            template.opsForValue().set("key-$i", UUID.randomUUID().toString())
        }

        (1..500).toList().forEach { i ->
            if (template.opsForValue().get("key-$i") != null) println("Found")
            else println("evicted")
        }

        get("constant-key-3")
        get("key-1")
        get("key-400")

        get("constant-key-3")
        get("key-100")
        get("key-200")

        (501..1500).toList().forEach { i ->
            template.opsForValue().set("key-$i", UUID.randomUUID().toString())
        }

        get("constant-key-1")
        get("constant-key-2")
        get("constant-key-3")

        get("key-1")
        get("key-1499")
    }

    fun allKeysLfu() {
        template.opsForValue().set("constant-key-1", UUID.randomUUID().toString())
        template.opsForValue().set("constant-key-2", UUID.randomUUID().toString())
        template.opsForValue().set("constant-key-3", UUID.randomUUID().toString())

        (1..500).toList().forEach { i ->
            template.opsForValue().set("key-$i", UUID.randomUUID().toString())
        }

        (1..500).toList().forEach { i ->
            get(i.toString())
        }

        get("constant-key-3")
        get("key-1")
        get("key-400")

        get("constant-key-3")
        get("key-100")
        get("key-200")

        (501..1500).toList().forEach { i ->
            template.opsForValue().set("key-$i", UUID.randomUUID().toString())
        }

        (1501..2000).toList().forEach { i ->
            template.opsForValue().set("key-$i", UUID.randomUUID().toString())
        }

        get("constant-key-1")
        get("constant-key-2")
        get("constant-key-3")

        get("key-1")
        get("key-1499")
    }

    fun volatilePolicies() {
        template.opsForValue().set("constant-key-1", UUID.randomUUID().toString(), 10, TimeUnit.MILLISECONDS)
        template.opsForValue().set("constant-key-2", UUID.randomUUID().toString(), 10, TimeUnit.MILLISECONDS)
        template.opsForValue().set("constant-key-3", UUID.randomUUID().toString(), 10, TimeUnit.MILLISECONDS)

        get("constant-key-1")

        (1..500).toList().forEach { i ->
            template.opsForValue().set("key-$i", UUID.randomUUID().toString(), 10, TimeUnit.MILLISECONDS)
        }

        (3..100).toList().forEach { i ->
            get(i.toString())
        }

        (501..1500).toList().forEach { i ->
            template.opsForValue().set("key-$i", UUID.randomUUID().toString(), 10, TimeUnit.MILLISECONDS)
        }

        get("constant-key-1")
        get("constant-key-2")
        get("constant-key-3")

        get("key-1")
        get("key-100")
        get("key-101")
        get("key-1499")
    }

    fun allKeysRandom() {
        template.opsForValue().set("constant-key-1", UUID.randomUUID().toString())
        template.opsForValue().set("constant-key-2", UUID.randomUUID().toString())
        template.opsForValue().set("constant-key-3", UUID.randomUUID().toString())

        (1..500).toList().forEach { i ->
            template.opsForValue().set("key-$i", UUID.randomUUID().toString())
        }

        (501..1500).toList().forEach { i ->
            template.opsForValue().set("key-$i", UUID.randomUUID().toString())
        }

        (1..1500).toList().forEach { i ->
            get(i.toString())
        }
    }
}

fun main(args: Array<String>) {
    runApplication<RedisApplication>(*args)
}