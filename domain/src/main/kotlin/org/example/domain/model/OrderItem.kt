package org.example.domain.model

import java.math.BigDecimal

data class OrderItem(
    val productId: Long,
    val quantity: Int,
    val unitPrice: BigDecimal
)
