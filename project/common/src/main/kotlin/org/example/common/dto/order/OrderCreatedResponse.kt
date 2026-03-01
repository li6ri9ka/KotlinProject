package org.example.common.dto.order

import kotlinx.serialization.Serializable

@Serializable
data class OrderCreatedResponse(
    val orderId: Long
)
