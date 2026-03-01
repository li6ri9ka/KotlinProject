package org.example.data.repository

import org.example.data.repository.impl.ExposedAuditLogRepository
import org.example.data.repository.impl.ExposedOrderRepository
import org.example.data.repository.impl.ExposedProductRepository

object RepositoryModule {
    fun productRepository() = ExposedProductRepository()
    fun orderRepository() = ExposedOrderRepository()
    fun auditLogRepository() = ExposedAuditLogRepository()
}
