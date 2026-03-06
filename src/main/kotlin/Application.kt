package com.example

import com.example.model.AuditLogs
import com.example.model.OrderItems
import com.example.model.Orders
import com.example.model.Products
import com.example.model.Users
import com.example.model.Users.email
import com.example.model.Users.password
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import com.example.cache.RedisClientProvider


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    RedisClientProvider.init("localhost", 6379)

    environment.monitor.subscribe(ApplicationStopped) {
        RedisClientProvider.shutdown()
    }

    configureSerialization()
    configureHTTP()
    configureSecurity()
    configureRouting()
    configureDatabase()
}

@OptIn(ExperimentalSerializationApi::class)
private fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
            explicitNulls = false
        })
    }
}

private fun Application.configureDatabase() {
    Database.connect("jdbc:sqlite:data.db", driver = "org.sqlite.JDBC")

    transaction {
        SchemaUtils.create(Users, Orders, Products, OrderItems, AuditLogs)

        if (Users.selectAll().empty()) {
            val hashedPassword = at.favre.lib.crypto.bcrypt.BCrypt.withDefaults().hashToString(12, "admin".toCharArray())
            Users.insert {
                it[email] = "admin@example.com"
                it[password] = hashedPassword
            }
        }
    }
}
