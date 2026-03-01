package org.example.domain.model

import java.time.Instant

data class AuditLog(
    val id: Long,
    val userId: Long?,
    val action: String,
    val payload: String,
    val createdAt: Instant
)
