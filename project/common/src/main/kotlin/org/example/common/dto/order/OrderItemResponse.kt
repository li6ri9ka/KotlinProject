package org.example.common.dto.order

import kotlinx.serialization.Serializable

@Serializable
data class OrderItemResponse(
    val productId: Long,
    val quantity: Int,
    val unitPrice: String
)
