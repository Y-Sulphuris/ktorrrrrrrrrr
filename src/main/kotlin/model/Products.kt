package com.example.model

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Products : Table() {
    val id = uuid("id").autoGenerate()
    val name = text("name")
    val description = text("description")
    val price = integer("price")
    val stock = integer("stock")
    val createdAt = datetime("created_at").default(LocalDateTime.now())

    override val primaryKey = PrimaryKey(id)
}