package org.example.data.db.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone

object OrdersTable : LongIdTable("orders") {
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.RESTRICT).index("ix_orders_user_id")
    val status = varchar("status", 20)
    val total = decimal("total", 12, 2)
    val createdAt = timestampWithTimeZone("created_at").index("ix_orders_created_at")
    val updatedAt = timestampWithTimeZone("updated_at")
}
