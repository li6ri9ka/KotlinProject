package org.example.app.di

import org.example.app.auth.BCryptPasswordHasher
import org.example.app.auth.JwtConfig
import org.example.app.auth.JwtTokenService
import org.example.app.controller.AdminController
import org.example.app.controller.AuthController
import org.example.app.controller.OrderController
import org.example.app.controller.ProductController
import org.example.data.cache.CacheFacade
import org.example.data.cache.RedisConfig
import org.example.data.repository.impl.ExposedUserRepository
import org.example.data.service.DataServiceModule
import org.example.data.service.impl.AuthServiceImpl
import java.time.Clock

class AppContainer(
    jwtConfig: JwtConfig,
    private val cacheFacade: CacheFacade,
    private val redisConfig: RedisConfig
) {
    private val userRepository = ExposedUserRepository(Clock.systemUTC())
    private val passwordHasher = BCryptPasswordHasher()
    private val tokenService = JwtTokenService(jwtConfig = jwtConfig, clock = Clock.systemUTC())
    private val authService = AuthServiceImpl(
        userRepository = userRepository,
        passwordHasher = passwordHasher,
        tokenService = tokenService
    )

    private val productService = DataServiceModule.productService()
    private val orderService = DataServiceModule.orderService()
    private val statsService = DataServiceModule.statsService()

    val authController: AuthController = AuthController(authService)
    val productController: ProductController = ProductController(
        productService = productService,
        cacheFacade = cacheFacade,
        cacheTtlSeconds = redisConfig.productTtlSeconds
    )
    val orderController: OrderController = OrderController(
        orderService = orderService,
        cacheFacade = cacheFacade,
        cacheTtlSeconds = redisConfig.orderTtlSeconds
    )
    val adminController: AdminController = AdminController(productService, statsService, cacheFacade)
}
