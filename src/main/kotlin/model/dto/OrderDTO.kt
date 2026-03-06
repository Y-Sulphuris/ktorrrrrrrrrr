package com.example.model.dto

import java.time.LocalDateTime
import java.util.UUID

data class OrderDTO(val id: UUID, val user: UserDTO, val creationDate: LocalDateTime)