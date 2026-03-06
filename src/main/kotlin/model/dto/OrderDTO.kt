package com.example.model.dto

import com.example.service.UserService
import java.time.LocalDateTime
import java.util.UUID

data class OrderDTO(val id: UUID, val userId: UUID, val creationDate: LocalDateTime) {
    fun getUser(users: UserService) : UserDTO {
        return users.findById(userId)!!
    }
}