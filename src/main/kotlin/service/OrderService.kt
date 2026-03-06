package com.example.service

import com.example.model.Orders
import com.example.model.dto.OrderDTO
import com.example.model.dto.UserDTO
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

class OrderService {
    fun insertOrder(user: UserDTO) {
        val creationDate = LocalDateTime.now();
        transaction {
            val orderId = Orders.insert {
                it[Orders.userId] = user.id
                it[Orders.createdAt] = creationDate
            } get Orders.id

            OrderResponse(
                id = orderId.toString(),
                user = user.id.toString(),
                createdAt = "now"
            )
        }
    }

    fun getAllOrders(): List<OrderDTO> {
        return transaction {
            val query = Orders.selectAll()

            query.map {
                OrderDTO(it[Orders.id], it[Orders.userId], it[Orders.createdAt])
            }
        }
    }

    fun updateOrder(order: OrderDTO) {
        return transaction {
            Orders.update({ Orders.id eq order.id }) {
                it[Orders.userId] = order.userId;
                it[Orders.createdAt] = order.creationDate;
            }
        }
    }

    fun deleteOrder(order: OrderDTO) {
        return transaction {
            Orders.deleteWhere { Orders.id eq order.id } > 0
        }
    }
}

@Serializable
data class OrderResponse(
    var id: String,
    val user: String,
    val createdAt: String)