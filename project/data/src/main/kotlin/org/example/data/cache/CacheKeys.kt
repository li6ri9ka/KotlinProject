package org.example.data.cache

object CacheKeys {
    fun productById(productId: Long): String = "product:$productId"
    fun ordersByUser(userId: Long): String = "orders:user:$userId"
    fun orderById(orderId: Long): String = "order:$orderId"
}
