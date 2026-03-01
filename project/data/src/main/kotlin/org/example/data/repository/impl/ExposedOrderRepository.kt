package org.example.data.repository.impl

import org.example.data.db.tables.OrderItemsTable
import org.example.data.db.tables.OrdersTable
import org.example.domain.model.Order
import org.example.domain.model.OrderItem
import org.example.domain.model.OrderStatus
import org.example.domain.repository.OrderRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.Clock
import java.time.OffsetDateTime

class ExposedOrderRepository(
    private val clock: Clock = Clock.systemUTC()
) : OrderRepository {
    override fun create(order: Order): Order = transaction {
        val now = OffsetDateTime.now(clock)

        val orderId = OrdersTable.insert {
            it[userId] = order.userId
            it[status] = order.status.name
            it[total] = order.total
            it[createdAt] = now
            it[updatedAt] = now
        }[OrdersTable.id].value

        insertItems(orderId, order.items)

        loadOrder(orderId) ?: error("Failed to load created order")
    }

    override fun findById(orderId: Long): Order? = transaction {
        loadOrder(orderId)
    }

    override fun findByUser(userId: Long): List<Order> = transaction {
        val orderRows = OrdersTable
            .selectAll()
            .where { OrdersTable.userId eq userId }
            .toList()

        mapOrders(orderRows)
    }

    override fun update(order: Order): Order = transaction {
        val updatedRows = OrdersTable.update({ OrdersTable.id eq order.id }) {
            it[userId] = order.userId
            it[status] = order.status.name
            it[total] = order.total
            it[updatedAt] = OffsetDateTime.now(clock)
        }

        if (updatedRows == 0) {
            throw IllegalStateException("Order with id=${order.id} not found")
        }

        OrderItemsTable.deleteWhere { OrderItemsTable.orderId eq order.id }
        insertItems(order.id, order.items)

        loadOrder(order.id) ?: error("Failed to load updated order")
    }

    private fun insertItems(orderId: Long, items: List<OrderItem>) {
        items.forEach { item ->
            OrderItemsTable.insert {
                it[OrderItemsTable.orderId] = orderId
                it[productId] = item.productId
                it[quantity] = item.quantity
                it[unitPrice] = item.unitPrice
            }
        }
    }

    private fun loadOrder(orderId: Long): Order? {
        val orderRow = OrdersTable
            .selectAll()
            .where { OrdersTable.id eq orderId }
            .limit(1)
            .firstOrNull()
            ?: return null

        val items = OrderItemsTable
            .selectAll()
            .where { OrderItemsTable.orderId eq orderId }
            .map { it.toOrderItem() }

        return orderRow.toOrder(items)
    }

    private fun mapOrders(orderRows: List<ResultRow>): List<Order> {
        if (orderRows.isEmpty()) return emptyList()

        val ids = orderRows.map { it[OrdersTable.id].value }
        val itemsByOrderId = OrderItemsTable
            .selectAll()
            .where { OrderItemsTable.orderId inList ids }
            .groupBy { it[OrderItemsTable.orderId].value }
            .mapValues { (_, rows) -> rows.map { it.toOrderItem() } }

        return orderRows.map { row ->
            val id = row[OrdersTable.id].value
            row.toOrder(itemsByOrderId[id].orEmpty())
        }
    }

    private fun ResultRow.toOrder(items: List<OrderItem>): Order = Order(
        id = this[OrdersTable.id].value,
        userId = this[OrdersTable.userId].value,
        items = items,
        total = this[OrdersTable.total],
        status = runCatching { OrderStatus.valueOf(this[OrdersTable.status]) }
            .getOrDefault(OrderStatus.CREATED),
        createdAt = this[OrdersTable.createdAt].toInstant()
    )

    private fun ResultRow.toOrderItem(): OrderItem = OrderItem(
        productId = this[OrderItemsTable.productId].value,
        quantity = this[OrderItemsTable.quantity],
        unitPrice = this[OrderItemsTable.unitPrice]
    )
}
