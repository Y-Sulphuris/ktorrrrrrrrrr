package com.example.cache

import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection

object RedisClientProvider {

    private var client: RedisClient? = null
    private var connection: StatefulRedisConnection<String, String>? = null

    fun init(host: String, port: Int) {
        if (client == null) {
            client = RedisClient.create("redis://$host:$port")
            connection = client!!.connect()
        }
    }

    fun commands() = connection!!.sync()

    fun shutdown() {
        connection?.close()
        client?.shutdown()
    }
}