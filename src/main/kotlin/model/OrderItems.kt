package com.example.model

import org.jetbrains.exposed.sql.Table

object OrderItems : Table() {
    val id = uuid("order_items_id").autoGenerate()
    val productId = uuid("product_id").references(Products.id)
    val orderId = uuid("order_id").references(Orders.id)
    val count = integer("count")

    override val primaryKey = PrimaryKey(id)
}
