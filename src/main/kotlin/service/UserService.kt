package com.example.service

import com.example.model.Users
import com.example.model.dto.UserDTO
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

class UserService {
    fun insertUser(email: String, password: String) {
        val creationDate = LocalDateTime.now();
        transaction {
            val userId = Users.insert {
                it[Users.email] = email
                it[Users.password] = password
                it[Users.creationDate] = creationDate
            } get Users.id

            UserDTO(
                id = userId,
                email,
                password,
                creationDate
            )
        }
    }

    fun getAllUsers(): List<UserDTO> {
        return transaction {
            val query = Users.selectAll()

            query.map {
                UserDTO(it[Users.id], it[Users.email], it[Users.password], it[Users.creationDate])
            }
        }
    }

    fun updateUser(user: UserDTO) {
        return transaction {
            Users.update({ Users.id eq user.id }) {
                it[Users.email] = user.email;
                it[Users.password] = user.password;
                it[Users.creationDate] = user.creationDate;
            }
        }
    }

    fun deleteUser(user: UserDTO) {
        return transaction {
            Users.deleteWhere { Users.id eq user.id } > 0
        }
    }

    fun findByEmail(email: String): UserDTO? {
        return transaction {
            Users
                .select { Users.email eq email }
                .singleOrNull()
                ?.let {
                    UserDTO(
                        id = it[Users.id],
                        email = it[Users.email],
                        password = it[Users.password],
                        creationDate = it[Users.creationDate]
                    )
                }
        }
    }
}