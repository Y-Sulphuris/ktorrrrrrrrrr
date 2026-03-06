package com.example.service

import com.example.model.AuditLogs
import com.example.model.OrderItems
import com.example.model.dto.AuditLogsDTO
import com.example.model.dto.OrderItemsDTO
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class AuditLogsService {
    fun log(text: String) {
        val creationDate = LocalDateTime.now();
        transaction {
            val logId = AuditLogs.insert {
                it[AuditLogs.description] = text
            } get AuditLogs.id

            AuditLogsDTO(
                id = logId,
                description = text,
                creationDate = creationDate
            )
        }
    }
}