package org.example.data.db.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone

object AuditLogsTable : LongIdTable("audit_logs") {
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.SET_NULL).nullable().index("ix_audit_logs_user_id")
    val action = varchar("action", length = 100).index("ix_audit_logs_action")
    val payload = text("payload")
    val createdAt = timestampWithTimeZone("created_at").index("ix_audit_logs_created_at")
}
