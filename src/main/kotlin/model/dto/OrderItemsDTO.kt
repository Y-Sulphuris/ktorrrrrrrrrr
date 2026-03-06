package com.example.model.dto

import java.util.UUID

data class OrderItemsDTO(val id: UUID, val product: UUID, val order: UUID, val count: Int)