package com.example.model

import com.example.model.Orders.id
import com.example.model.dto.OrderDTO
import com.example.model.dto.ProductDTO
import com.example.model.dto.UserDTO
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.util.UUID

object Orders : Table() {
    val id = uuid("id").autoGenerate()
    val userId = uuid("user_id").references(Users.id)
    val createdAt = datetime("created_at").default(LocalDateTime.now())

    override val primaryKey = PrimaryKey(id)
}
