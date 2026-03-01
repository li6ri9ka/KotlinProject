package org.example.domain.repository

import org.example.domain.model.AuditLog

interface AuditLogRepository {
    fun create(log: AuditLog): AuditLog
}
