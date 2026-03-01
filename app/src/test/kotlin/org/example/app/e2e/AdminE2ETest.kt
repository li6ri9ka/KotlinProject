package org.example.app.e2e

import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.example.app.auth.JwtConfig
import org.example.app.auth.JwtTokenService
import org.example.app.auth.configureSecurity
import org.example.app.controller.AdminController
import org.example.app.plugins.configureErrorHandling
import org.example.app.plugins.configureSerialization
import org.example.app.routes.configureAdminRoutes
import org.example.data.cache.NoOpCacheFacade
import org.example.domain.model.Product
import org.example.domain.model.Role
import org.example.domain.service.ProductService
import org.example.domain.service.StatsService
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

class AdminE2ETest {
    @Test
    fun `user role cannot access admin stats`() = testApplication {
        val jwtConfig = testJwtConfig()
        val tokenService = JwtTokenService(jwtConfig)
        val userToken = tokenService.issueToken(userId = 10, email = "user@example.com", role = Role.USER)

        application {
            configureSerialization()
            configureErrorHandling()
            configureSecurity(jwtConfig)
            configureAdminRoutes(adminController())
        }

        val response = client.get("/stats/orders") {
            bearerAuth(userToken)
        }

        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    @Test
    fun `admin role can access stats`() = testApplication {
        val jwtConfig = testJwtConfig()
        val tokenService = JwtTokenService(jwtConfig)
        val adminToken = tokenService.issueToken(userId = 1, email = "admin@example.com", role = Role.ADMIN)

        application {
            configureSerialization()
            configureErrorHandling()
            configureSecurity(jwtConfig)
            configureAdminRoutes(adminController())
        }

        val response = client.get("/stats/orders") {
            bearerAuth(adminToken)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val json = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        assertEquals("2", json["totalOrders"]?.jsonPrimitive?.content)
        assertEquals("150.00", json["totalRevenue"]?.jsonPrimitive?.content)
    }

    private fun testJwtConfig() = JwtConfig(
        secret = "test-secret",
        issuer = "shop-service",
        audience = "shop-clients",
        realm = "shop-api",
        expiresInSeconds = 3600
    )

    private fun adminController(): AdminController {
        val productService = object : ProductService {
            override fun listProducts(): List<Product> = emptyList()
            override fun getProduct(productId: Long): Product = error("unused")
            override fun createProduct(product: Product): Product = error("unused")
            override fun updateProduct(product: Product): Product = error("unused")
            override fun deleteProduct(productId: Long) = Unit
        }

        val statsService = object : StatsService {
            override fun getOrdersStats(): Map<String, Any> = mapOf(
                "totalOrders" to 2L,
                "canceledOrders" to 1L,
                "createdOrders" to 1L,
                "totalRevenue" to BigDecimal("150.00")
            )
        }

        return AdminController(
            productService = productService,
            statsService = statsService,
            cacheFacade = NoOpCacheFacade
        )
    }
}
