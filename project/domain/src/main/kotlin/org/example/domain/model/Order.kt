package org.example.domain.model

import java.math.BigDecimal
import java.time.Instant

data class Order(
    val id: Long,
    val userId: Long,
    val items: List<OrderItem>,
    val total: BigDecimal,
    val status: OrderStatus,
    val createdAt: Instant
)

enum class OrderStatus {
    CREATED,
    CANCELED
}
