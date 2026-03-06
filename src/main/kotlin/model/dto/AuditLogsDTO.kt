package com.example.model.dto

import java.time.LocalDateTime
import java.util.UUID

data class AuditLogsDTO(val id: UUID, val description: String, val creationDate: LocalDateTime)