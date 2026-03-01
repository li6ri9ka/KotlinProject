package org.example.common.dto.order

import kotlinx.serialization.Serializable

@Serializable
data class OrderResponse(
    val id: Long,
    val status: String,
    val total: String,
    val createdAt: String,
    val items: List<OrderItemResponse>
)
