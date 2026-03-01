package org.example.data.db.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone

object ProductsTable : LongIdTable("products") {
    val name = varchar("name", 255).index("ix_products_name")
    val description = text("description")
    val price = decimal("price", 12, 2)
    val stock = integer("stock")
    val createdAt = timestampWithTimeZone("created_at")
    val updatedAt = timestampWithTimeZone("updated_at")
}
