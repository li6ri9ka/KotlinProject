package org.example.data.db.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object OrderItemsTable : LongIdTable("order_items") {
    val orderId = reference("order_id", OrdersTable, onDelete = ReferenceOption.CASCADE).index("ix_order_items_order_id")
    val productId = reference("product_id", ProductsTable, onDelete = ReferenceOption.RESTRICT).index("ix_order_items_product_id")
    val quantity = integer("quantity")
    val unitPrice = decimal("unit_price", 12, 2)

    init {
        uniqueIndex("ux_order_items_order_product", orderId, productId)
    }
}
