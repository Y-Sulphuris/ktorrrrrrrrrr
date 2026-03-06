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
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.util.UUID

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

                emailQueue.send(
                    EmailMessage(
                        "admin@example.com",
                        "Кто-то залогинился",
                        "Кто-то залогинился!!! (я не знаю зачем этот воркер но так надо)"
                    )
                )

                call.respond(mapOf("token" to token))
            }

            post("register") {
                val request = call.receive<RegisterRequest>()

                val hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt())

                userService.insertUser(
                    email = request.email,
                    password = hashedPassword
                )

                call.respond(HttpStatusCode.Created)
            }
        }

//        authentication {
        route("products") {
            get {
                val products = productService.getAllProducts()
                call.respond(products)
            }

            get("{id}") {
                val id = call.parameters["id"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing id")

                val productId = try {
                    UUID.fromString(id)
                } catch (e: Exception) {
                    return@get call.respond(HttpStatusCode.BadRequest, "Invalid id")
                }

                val product = productService.getProductById(productId.toString())
                    ?: return@get call.respond(HttpStatusCode.NotFound, "Product not found")

                call.respond(product)
            }

            post {
                val request = call.receive<CreateProductRequest>()

                val productId = UUID.randomUUID()
                transaction {
                    Products.insert {
                        it[Products.id] = productId
                        it[Products.name] = request.name
                        it[Products.description] = request.description
                        it[Products.price] = request.price
                        it[Products.stock] = request.stock
                        it[Products.createdAt] = java.time.LocalDateTime.now()
                    }
                }

                call.respond(
                    HttpStatusCode.Created,
                    mapOf(
                        "id" to productId.toString(),
                        "name" to request.name,
                        "description" to request.description,
                        "price" to request.price.toString()
                    )
                )
            }
        }

        route("orders") {
            post {
                val request = call.receive<CreateOrderRequest>()

                val products = request.items.map { item ->
                    val product = productService.findProductById(UUID.fromString(item.productId))
                        ?: return@post call.respond(
                            HttpStatusCode.BadRequest,
                            "Product ${item.productId} not found"
                        )

                    if (product.price < item.count) {
                        return@post call.respond(
                            HttpStatusCode.BadRequest,
                            "Not enough stock for ${product.id}"
                        )
                    }
                    product
                }

                val orderId = UUID.randomUUID()
                try {
                    transaction {
                        Orders.insert {
                            it[Orders.id] = orderId
                            it[Orders.userId] = UUID.fromString(request.userId)
                            it[Orders.createdAt] = java.time.LocalDateTime.now()
                        }

                        request.items.forEach { item ->
                            OrderItems.insert {
                                it[OrderItems.id] = UUID.randomUUID()
                                it[OrderItems.orderId] = orderId
                                it[OrderItems.productId] = UUID.fromString(item.productId)
                                it[OrderItems.count] = item.count
                            }

                            productService.decreaseStock(UUID.fromString(item.productId), item.count)
                        }
                    }
                } catch (e: IllegalArgumentException) {
                }

                emailQueue.send(
                    EmailMessage(
                        "admin@example.com",
                        "Кто-то создал ордер",
                        "${orderId}, ${request.userId}, ${request.items.map { it.productId to it.count }}"
                    )
                )

                call.respond(HttpStatusCode.Created, mapOf("orderId" to orderId.toString()))
            }

            get {
                val orders = orderService.getAllOrders()
                call.respond(orders)
            }

            delete("{id}") {
                val id = call.parameters["id"]
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing id")

                val orderId = try {
                    UUID.fromString(id)
                } catch (e: Exception) {
                    return@delete call.respond(HttpStatusCode.BadRequest, "Invalid id")
                }

                orderService.deleteOrder(orderId)
                call.respond(HttpStatusCode.OK, "Deleted")
            }
        }
//        }

        get("/") {
            call.respondText("Hello World!")
        }
    }
}

@Serializable
data class CreateProductRequest(
    val name: String,
    val description: String,
    val price: Int,
    val stock: Int
)