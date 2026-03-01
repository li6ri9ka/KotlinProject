package org.example.data.service

import org.example.data.cache.CacheFacade
import org.example.data.cache.NoOpCacheFacade
import org.example.data.cache.RedisCacheFacade
import org.example.data.cache.RedisClientFactory
import org.example.data.cache.RedisConfig
import org.example.data.queue.NoOpOrderEventPublisher
import org.example.data.queue.OrderEventPublisher
import org.example.data.queue.RabbitMqConfig
import org.example.data.queue.RabbitMqOrderEventPublisher
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

    fun cacheFacade(redisConfig: RedisConfig): CacheFacade {
        if (!redisConfig.enabled) return NoOpCacheFacade
        return RedisCacheFacade(RedisClientFactory.create(redisConfig))
    }

    fun orderEventPublisher(rabbitMqConfig: RabbitMqConfig): OrderEventPublisher {
        if (!rabbitMqConfig.enabled) return NoOpOrderEventPublisher
        return RabbitMqOrderEventPublisher(rabbitMqConfig)
    }
}
