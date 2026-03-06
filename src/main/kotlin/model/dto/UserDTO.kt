package com.example.model.dto

import java.time.LocalDateTime
import java.util.UUID

data class UserDTO(val id: UUID, val email: String, val password: String, val creationDate: LocalDateTime)
