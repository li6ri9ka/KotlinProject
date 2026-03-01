package org.example.data.service.impl

import org.example.common.dto.event.OrderEventMessage
import org.example.common.dto.event.OrderEventType
import org.example.data.queue.OrderEventPublisher
import org.example.domain.error.AccessDeniedException
import org.example.domain.error.NotFoundException
import org.example.domain.error.ValidationException
import org.example.domain.model.AuditLog
import org.example.domain.model.Order
import org.example.domain.model.OrderItem
import org.example.domain.model.OrderStatus
import org.example.domain.repository.AuditLogRepository
import org.example.domain.repository.OrderRepository
import org.example.domain.repository.ProductRepository
import org.example.domain.service.OrderService
import java.math.BigDecimal
import java.time.Clock

class OrderServiceImpl(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val auditLogRepository: AuditLogRepository,
    private val eventPublisher: OrderEventPublisher,
    private val clock: Clock = Clock.systemUTC()
) : OrderService {
    override fun createOrder(userId: Long, items: List<OrderItem>): Order {
        if (items.isEmpty()) throw ValidationException("Order must contain at least one item")

        val validated = items.map { requested ->
            if (requested.quantity <= 0) {
                throw ValidationException("Quantity must be greater than zero")
            }

            val product = productRepository.findById(requested.productId)
                ?: throw NotFoundException("Product not found: ${requested.productId}")

            if (product.stock < requested.quantity) {
                throw ValidationException("Insufficient stock for product ${product.id}")
            }

            requested.copy(unitPrice = product.price)
        }

        val total = validated.fold(BigDecimal.ZERO) { acc, item ->
            acc + item.unitPrice.multiply(item.quantity.toBigDecimal())
        }

        val draft = Order(
            id = 0,
            userId = userId,
            items = validated,
            total = total,
            status = OrderStatus.CREATED,
            createdAt = clock.instant()
        )

        val created = orderRepository.create(draft)

        validated.forEach { item ->
            val product = productRepository.findById(item.productId)
                ?: throw NotFoundException("Product not found: ${item.productId}")
            productRepository.update(product.copy(stock = product.stock - item.quantity))
        }

        auditLogRepository.create(
            AuditLog(
                id = 0,
                userId = userId,
                action = "ORDER_CREATED",
                payload = "orderId=${created.id}",
                createdAt = clock.instant()
            )
        )

        eventPublisher.publish(
            OrderEventMessage(
                eventType = OrderEventType.ORDER_CREATED,
                orderId = created.id,
                userId = userId,
                createdAt = clock.instant().toString(),
                payload = "total=${created.total}"
            )
        )

        return created
    }

    override fun getOrders(userId: Long): List<Order> = orderRepository.findByUser(userId)

    override fun cancelOrder(userId: Long, orderId: Long) {
        val order = orderRepository.findById(orderId) ?: throw NotFoundException("Order not found: $orderId")
        if (order.userId != userId) {
            throw AccessDeniedException("You cannot cancel this order")
        }
        if (order.status == OrderStatus.CANCELED) return

        order.items.forEach { item ->
            val product = productRepository.findById(item.productId)
                ?: throw NotFoundException("Product not found: ${item.productId}")
            productRepository.update(product.copy(stock = product.stock + item.quantity))
        }

        orderRepository.update(order.copy(status = OrderStatus.CANCELED))

        auditLogRepository.create(
            AuditLog(
                id = 0,
                userId = userId,
                action = "ORDER_CANCELED",
                payload = "orderId=$orderId",
                createdAt = clock.instant()
            )
        )

        eventPublisher.publish(
            OrderEventMessage(
                eventType = OrderEventType.ORDER_CANCELED,
                orderId = orderId,
                userId = userId,
                createdAt = clock.instant().toString(),
                payload = "status=CANCELED"
            )
        )
    }
}
