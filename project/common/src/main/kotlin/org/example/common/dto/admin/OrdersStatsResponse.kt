package org.example.common.dto.admin

import kotlinx.serialization.Serializable

@Serializable
data class OrdersStatsResponse(
    val totalOrders: Long,
    val canceledOrders: Long,
    val createdOrders: Long,
    val totalRevenue: String
)
