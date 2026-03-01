package org.example.app.integration

import org.example.app.testsupport.IntegrationContainers
import org.example.data.db.tables.OrderItemsTable
import org.example.data.db.tables.OrdersTable
import org.example.data.db.tables.ProductsTable
import org.example.data.db.tables.UsersTable
import org.example.data.repository.impl.ExposedOrderRepository
import org.example.data.repository.impl.ExposedProductRepository
import org.example.domain.model.Order
import org.example.domain.model.OrderItem
import org.example.domain.model.OrderStatus
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.time.OffsetDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OrderIntegrationTest : IntegrationContainers() {
    private val orderRepo = ExposedOrderRepository()
    private val productRepo = ExposedProductRepository()

    @BeforeEach
    fun cleanup() {
        transaction {
            OrderItemsTable.deleteAll()
            OrdersTable.deleteAll()
            ProductsTable.deleteAll()
            UsersTable.deleteAll()
        }
    }

    @Test
    fun `create and list order with items via postgres`() {
        val userId = transaction {
            UsersTable.insert {
                it[email] = "user@example.com"
                it[passwordHash] = "hash"
                it[role] = "USER"
                it[createdAt] = OffsetDateTime.now()
            }[UsersTable.id].value
        }

        val product = productRepo.create(
            org.example.domain.model.Product(
                id = 0,
                name = "Laptop",
                description = "Ultrabook",
                price = BigDecimal("1200.00"),
                stock = 5
            )
        )

        val created = orderRepo.create(
            Order(
                id = 0,
                userId = userId,
                items = listOf(OrderItem(product.id, 2, BigDecimal("1200.00"))),
                total = BigDecimal("2400.00"),
                status = OrderStatus.CREATED,
                createdAt = Instant.now()
            )
        )

        val userOrders = orderRepo.findByUser(userId)
        assertEquals(1, userOrders.size)
        assertEquals(created.id, userOrders.first().id)
        assertTrue(userOrders.first().items.isNotEmpty())
        assertEquals(product.id, userOrders.first().items.first().productId)
    }
}
