package org.example.common.dto.event

import kotlinx.serialization.Serializable

@Serializable
data class OrderEventMessage(
    val eventType: OrderEventType,
    val orderId: Long,
    val userId: Long,
    val createdAt: String,
    val payload: String
)

@Serializable
enum class OrderEventType {
    ORDER_CREATED,
    ORDER_CANCELED
}
