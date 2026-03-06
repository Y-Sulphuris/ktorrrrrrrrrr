package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.model.AuditLogs
import com.example.model.OrderItems
import com.example.model.Orders
import com.example.model.Products
import com.example.model.Users
import com.example.model.Users.email
import com.example.model.Users.password
import com.example.model.dto.UserDTO
import com.example.service.OrderService
import com.example.service.ProductService
import com.example.service.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.openapi.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

fun Application.configureRouting() {
    val orderService = OrderService()
    val jwtService = JwtService
    val productService = ProductService()
    val userService = UserService()

    routing {
        route("auth") {
            post("login") {

                val request = call.receive<LoginRequest>()

                val user = userService.findByEmail(request.email)
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)

                if (!BCrypt.checkpw(request.password, user.password)) {
                    return@post call.respond(HttpStatusCode.Unauthorized)
                }

                val token = JwtService.generateToken(user.id.toString())

                call.respond(mapOf("token" to token))
            }
            post("register") {

                val request = call.receive<RegisterRequest>()

                val hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt())

                val user = userService.insertUser(
                    email = request.email,
                    password = hashedPassword
                )

                call.respond(HttpStatusCode.Created)
            }
        }
        route("products") {
            get {

            }
            get("products/{id}") {

            }
        }
        route("orders") {
            post {

            }
            get {

            }
            delete("{id}") {

            }
        }
        get("/") {
            call.respondText("Hello World!")
        }
    }
}
