package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.openapi.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        route("auth") {
            post("login") {

            }
            post("register") {

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

/*
    users
    products
    orders
    order_items
    audit_logs

 */