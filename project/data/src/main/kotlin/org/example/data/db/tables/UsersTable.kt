package org.example.data.db.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone

object UsersTable : LongIdTable("users") {
    val email = varchar("email", 320).uniqueIndex("ux_users_email")
    val passwordHash = varchar("password_hash", 255)
    val role = varchar("role", 20)
    val createdAt = timestampWithTimeZone("created_at")
}
