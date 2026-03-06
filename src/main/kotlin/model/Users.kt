package com.example.model

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.util.UUID

object Users : Table() {
    val id = uuid("id").autoIncrement()
    val name = varchar("email", 32).uniqueIndex()
    val email = varchar("password", 255)
    val creationDate = datetime("creation_date").default(LocalDateTime.now())

    override val primaryKey = PrimaryKey(id)
}
