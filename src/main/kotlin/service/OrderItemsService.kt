package com.example.service

import com.example.model.OrderItems
import com.example.model.dto.OrderDTO
import com.example.model.dto.ProductDTO
import com.example.model.dto.OrderItemsDTO
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

class OrderItemsService {
    fun insertOrderItem(product: ProductDTO, order: OrderDTO, count: Int) {
        transaction {
            val orderItemId = OrderItems.insert {
                it[OrderItems.productId] = product.id
                it[OrderItems.orderId] = order.id
                it[OrderItems.count] = count
            } get OrderItems.id

            OrderItemsDTO(
                id = orderItemId,
                product.id,
                order.id,
                count
            )
        }
    }

    fun getAllOrderItems(): List<OrderItemsDTO> {
        return transaction {
            val query = OrderItems.selectAll()

            query.map {
                OrderItemsDTO(it[OrderItems.id], it[OrderItems.productId], it[OrderItems.orderId], it[OrderItems.count])
            }
        }
    }

    fun updateOrderItem(orderItem: OrderItemsDTO) {
        return transaction {
            OrderItems.update({ OrderItems.id eq orderItem.id }) {
                it[OrderItems.orderId] = orderItem.order;
                it[OrderItems.productId] = orderItem.product;
                it[OrderItems.count] = orderItem.count;
            }
        }
    }

    fun deleteOrderItem(orderItem: OrderItemsDTO) {
        return transaction {
            OrderItems.deleteWhere { OrderItems.id eq orderItem.id } > 0
        }
    }
}