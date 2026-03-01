package org.example.app.controller

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.example.common.dto.order.CreateOrderRequest
import org.example.common.dto.order.OrderCreatedResponse
import org.example.common.dto.order.OrderItemResponse
import org.example.common.dto.order.OrderResponse
import org.example.data.cache.CacheFacade
import org.example.data.cache.CacheKeys
import org.example.domain.model.OrderItem
import org.example.domain.service.OrderService

class OrderController(
    private val orderService: OrderService,
    private val cacheFacade: CacheFacade,
    private val cacheTtlSeconds: Long
) {
    private val json = Json { ignoreUnknownKeys = true }

    fun createOrder(userId: Long, request: CreateOrderRequest): OrderCreatedResponse {
        val created = orderService.createOrder(
            userId = userId,
            items = request.items.map { OrderItem(it.productId, it.quantity, java.math.BigDecimal.ZERO) }
        )

        cacheFacade.invalidate(CacheKeys.ordersByUser(userId))
        request.items.forEach { item ->
            cacheFacade.invalidate(CacheKeys.productById(item.productId))
        }
        cacheFacade.invalidate(CacheKeys.PRODUCTS_ALL)

        val createdResponse = OrderCreatedResponse(created.id)
        cacheFacade.set(
            CacheKeys.orderById(created.id),
            json.encodeToString(OrderCreatedResponse.serializer(), createdResponse),
            cacheTtlSeconds
        )

        return createdResponse
    }

    fun getOrders(userId: Long): List<OrderResponse> {
        cacheFacade.get(CacheKeys.ordersByUser(userId))?.let { cached ->
            runCatching {
                return json.decodeFromString(ListSerializer(OrderResponse.serializer()), cached)
            }
        }

        val orders = orderService.getOrders(userId).map { order ->
            OrderResponse(
                id = order.id,
                status = order.status.name,
                total = order.total.toPlainString(),
                createdAt = order.createdAt.toString(),
                items = order.items.map { item ->
                    OrderItemResponse(
                        productId = item.productId,
                        quantity = item.quantity,
                        unitPrice = item.unitPrice.toPlainString()
                    )
                }
            )
        }

        cacheFacade.set(
            CacheKeys.ordersByUser(userId),
            json.encodeToString(ListSerializer(OrderResponse.serializer()), orders),
            cacheTtlSeconds
        )

        return orders
    }

    fun cancelOrder(userId: Long, orderId: Long) {
        orderService.cancelOrder(userId, orderId)
        cacheFacade.invalidate(CacheKeys.ordersByUser(userId))
        cacheFacade.invalidate(CacheKeys.orderById(orderId))
        cacheFacade.invalidate(CacheKeys.PRODUCTS_ALL)
    }
}
