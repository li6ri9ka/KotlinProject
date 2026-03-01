package org.example.data.repository.impl

import org.example.data.db.tables.AuditLogsTable
import org.example.domain.model.AuditLog
import org.example.domain.repository.AuditLogRepository
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Clock
import java.time.OffsetDateTime

class ExposedAuditLogRepository(
    private val clock: Clock = Clock.systemUTC()
) : AuditLogRepository {
    override fun create(log: AuditLog): AuditLog = transaction {
        val inserted = AuditLogsTable.insert {
            it[userId] = log.userId
            it[action] = log.action
            it[payload] = log.payload
            it[createdAt] = OffsetDateTime.now(clock)
        }
        log.copy(id = inserted[AuditLogsTable.id].value)
    }
}
