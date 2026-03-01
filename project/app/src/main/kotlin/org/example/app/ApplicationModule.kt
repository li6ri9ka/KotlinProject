package org.example.app

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.callloging.CallLogging
import org.example.app.auth.configureSecurity
import org.example.app.config.AppEnvironment
import org.example.app.controller.OrderController
import org.example.app.controller.ProductController
import org.example.app.plugins.configureErrorHandling
import org.example.app.plugins.configureSerialization
import org.example.app.routes.configureOrderRoutes
import org.example.app.routes.configureProductRoutes
import org.example.data.db.config.DatabaseFactory
import org.example.data.service.DataServiceModule

fun Application.module() {
    DatabaseFactory.init(AppEnvironment.dbConfig())

    val productController = ProductController(DataServiceModule.productService())
    val orderController = OrderController(DataServiceModule.orderService())

    install(CallLogging)
    configureSerialization()
    configureErrorHandling()
    configureSecurity(AppEnvironment.jwtConfig())
    configureProductRoutes(productController)
    configureOrderRoutes(orderController)
}
