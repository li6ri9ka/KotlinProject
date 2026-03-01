package org.example.app

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.callloging.CallLogging
import org.example.app.auth.configureSecurity
import org.example.app.config.AppEnvironment
import org.example.app.di.AppContainer
import org.example.app.plugins.configureErrorHandling
import org.example.app.plugins.configureSerialization
import org.example.app.routes.configureAdminRoutes
import org.example.app.routes.configureAuthRoutes
import org.example.app.routes.configureOrderRoutes
import org.example.app.routes.configureProductRoutes
import org.example.data.db.config.DatabaseFactory
import org.example.data.service.DataServiceModule

fun Application.module() {
    val jwtConfig = AppEnvironment.jwtConfig()
    val dbConfig = AppEnvironment.dbConfig()
    val redisConfig = AppEnvironment.redisConfig()
    val rabbitMqConfig = AppEnvironment.rabbitMqConfig()

    DatabaseFactory.init(dbConfig)
    val cacheFacade = DataServiceModule.cacheFacade(redisConfig)
    val orderEventPublisher = DataServiceModule.orderEventPublisher(rabbitMqConfig)

    val container = AppContainer(jwtConfig, cacheFacade, redisConfig, orderEventPublisher)

    install(CallLogging)
    configureSerialization()
    configureErrorHandling()
    configureSecurity(jwtConfig)

    configureAuthRoutes(container.authController)
    configureProductRoutes(container.productController)
    configureOrderRoutes(container.orderController)
    configureAdminRoutes(container.adminController)
}
