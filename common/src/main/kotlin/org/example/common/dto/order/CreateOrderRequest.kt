package org.example.common.dto.order

import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderRequest(
    val items: List<CreateOrderItemRequest>
)
