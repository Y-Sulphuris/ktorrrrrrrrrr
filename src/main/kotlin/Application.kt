package com.example

import com.example.cache.RedisClientProvider
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    RedisClientProvider.init("localhost", 6379)

    environment.monitor.subscribe(ApplicationStopped) {
        RedisClientProvider.shutdown()
    }

    configureHTTP()
    configureSecurity()
    configureRouting()
}