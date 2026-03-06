package com.example.model.dto

import java.time.LocalDateTime
import java.util.UUID

data class ProductDTO(val id: UUID, val name: String, val description: String, val price: Int, val stock: Int, val creationDate: LocalDateTime)