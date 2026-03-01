package org.example.data.service

import org.example.data.repository.RepositoryModule
import org.example.data.service.impl.OrderServiceImpl
import org.example.data.service.impl.ProductServiceImpl
import org.example.data.service.impl.StatsServiceImpl

object DataServiceModule {
    fun productService() = ProductServiceImpl(RepositoryModule.productRepository())

    fun orderService() = OrderServiceImpl(
        orderRepository = RepositoryModule.orderRepository(),
        productRepository = RepositoryModule.productRepository(),
        auditLogRepository = RepositoryModule.auditLogRepository()
    )

    fun statsService() = StatsServiceImpl(RepositoryModule.orderRepository())
}
