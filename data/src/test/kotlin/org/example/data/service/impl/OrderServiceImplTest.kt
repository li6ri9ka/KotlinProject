package org.example.data.service.impl

import org.example.common.dto.event.OrderEventMessage
import org.example.common.dto.event.OrderEventType
import org.example.data.queue.OrderEventPublisher
import org.example.domain.error.ValidationException
import org.example.domain.model.AuditLog
import org.example.domain.model.Order
import org.example.domain.model.OrderItem
import org.example.domain.model.OrderStatus
import org.example.domain.model.Product
import org.example.domain.repository.AuditLogRepository
import org.example.domain.repository.OrderRepository
import org.example.domain.repository.ProductRepository
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class OrderServiceImplTest {
    @Test
    fun `createOrder throws on insufficient stock and skips side effects`() {
        val orderRepository = InMemoryOrderRepository()
        val productRepository = InMemoryProductRepository(
            mutableMapOf(
                1L to Product(1L, "Phone", "Smartphone", BigDecimal("200.00"), 1)
            )
        )
        val auditLogRepository = CapturingAuditLogRepository()
        val eventPublisher = CapturingOrderEventPublisher()
        val service = OrderServiceImpl(
            orderRepository = orderRepository,
            productRepository = productRepository,
            auditLogRepository = auditLogRepository,
            eventPublisher = eventPublisher,
            clock = fixedClock()
        )

        assertFailsWith<ValidationException> {
            service.createOrder(
                userId = 7L,
                items = listOf(OrderItem(productId = 1L, quantity = 2, unitPrice = BigDecimal.ZERO))
            )
        }

        assertEquals(0, orderRepository.createCalls)
        assertEquals(0, auditLogRepository.created.size)
        assertEquals(0, eventPublisher.events.size)
        assertEquals(1, productRepository.findById(1L)?.stock)
    }

    @Test
    fun `createOrder updates stock, writes audit and publishes event`() {
        val orderRepository = InMemoryOrderRepository()
        val productRepository = InMemoryProductRepository(
            mutableMapOf(
                1L to Product(1L, "Phone", "Smartphone", BigDecimal("200.00"), 5)
            )
        )
        val auditLogRepository = CapturingAuditLogRepository()
        val eventPublisher = CapturingOrderEventPublisher()
        val service = OrderServiceImpl(
            orderRepository = orderRepository,
            productRepository = productRepository,
            auditLogRepository = auditLogRepository,
            eventPublisher = eventPublisher,
            clock = fixedClock()
        )

        val created = service.createOrder(
            userId = 42L,
            items = listOf(OrderItem(productId = 1L, quantity = 2, unitPrice = BigDecimal.ZERO))
        )

        assertEquals(1L, created.id)
        assertEquals(BigDecimal("400.00"), created.total)
        assertEquals(OrderStatus.CREATED, created.status)
        assertEquals(3, productRepository.findById(1L)?.stock)
        assertEquals(1, auditLogRepository.created.size)
        assertEquals("ORDER_CREATED", auditLogRepository.created.first().action)
        assertEquals(1, eventPublisher.events.size)
        assertEquals(OrderEventType.ORDER_CREATED, eventPublisher.events.first().eventType)
        assertEquals(1L, eventPublisher.events.first().orderId)
    }

    private fun fixedClock(): Clock =
        Clock.fixed(Instant.parse("2026-03-01T10:00:00Z"), ZoneOffset.UTC)
}

private class InMemoryOrderRepository : OrderRepository {
    private val orders = linkedMapOf<Long, Order>()
    private var nextId = 1L
    var createCalls: Int = 0
        private set

    override fun create(order: Order): Order {
        createCalls++
        val persisted = order.copy(id = nextId++)
        orders[persisted.id] = persisted
        return persisted
    }

    override fun findById(orderId: Long): Order? = orders[orderId]

    override fun findByUser(userId: Long): List<Order> = orders.values.filter { it.userId == userId }

    override fun findAll(): List<Order> = orders.values.toList()

    override fun update(order: Order): Order {
        orders[order.id] = order
        return order
    }
}

private class InMemoryProductRepository(
    private val products: MutableMap<Long, Product>
) : ProductRepository {
    override fun findAll(): List<Product> = products.values.toList()

    override fun findById(id: Long): Product? = products[id]

    override fun create(product: Product): Product {
        products[product.id] = product
        return product
    }

    override fun update(product: Product): Product {
        products[product.id] = product
        return product
    }

    override fun delete(productId: Long) {
        products.remove(productId)
    }
}

private class CapturingAuditLogRepository : AuditLogRepository {
    val created = mutableListOf<AuditLog>()

    override fun create(log: AuditLog): AuditLog {
        created += log
        return log.copy(id = created.size.toLong())
    }
}

private class CapturingOrderEventPublisher : OrderEventPublisher {
    val events = mutableListOf<OrderEventMessage>()

    override fun publish(event: OrderEventMessage) {
        events += event
    }
}
