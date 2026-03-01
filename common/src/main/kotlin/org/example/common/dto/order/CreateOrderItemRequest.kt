package org.example.common.dto.order

import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderItemRequest(
    val productId: Long,
    val quantity: Int
)
