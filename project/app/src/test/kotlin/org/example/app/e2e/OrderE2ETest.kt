package org.example.app.e2e

import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import org.example.app.auth.JwtConfig
import org.example.app.auth.JwtTokenService
import org.example.app.auth.configureSecurity
import org.example.app.controller.OrderController
import org.example.app.plugins.configureSerialization
import org.example.app.routes.configureOrderRoutes
import org.example.data.cache.NoOpCacheFacade
import org.example.domain.model.Order
import org.example.domain.model.OrderItem
import org.example.domain.model.OrderStatus
import org.example.domain.model.Role
import org.example.domain.service.OrderService
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import kotlin.test.assertEquals

class OrderE2ETest {
    @Test
    fun `user creates and cancels order through API`() = testApplication {
        val jwtConfig = JwtConfig(
            secret = "test-secret",
            issuer = "shop-service",
            audience = "shop-clients",
            realm = "shop-api",
            expiresInSeconds = 3600
        )
        val tokenService = JwtTokenService(jwtConfig)
        val token = tokenService.issueToken(userId = 10, email = "user@example.com", role = Role.USER)

        val service = object : OrderService {
            private val orders = mutableListOf<Order>()

            override fun createOrder(userId: Long, items: List<OrderItem>): Order {
                val order = Order(
                    id = 101,
                    userId = userId,
                    items = items.map { it.copy(unitPrice = BigDecimal("100.00")) },
                    total = BigDecimal("100.00"),
                    status = OrderStatus.CREATED,
                    createdAt = Instant.parse("2026-03-01T12:00:00Z")
                )
                orders.add(order)
                return order
            }

            override fun getOrders(userId: Long): List<Order> = orders.filter { it.userId == userId }

            override fun cancelOrder(userId: Long, orderId: Long) {
                val idx = orders.indexOfFirst { it.id == orderId && it.userId == userId }
                if (idx >= 0) {
                    orders[idx] = orders[idx].copy(status = OrderStatus.CANCELED)
                }
            }
        }

        application {
            configureSerialization()
            configureSecurity(jwtConfig)
            configureOrderRoutes(
                OrderController(
                    orderService = service,
                    cacheFacade = NoOpCacheFacade,
                    cacheTtlSeconds = 60
                )
            )
        }

        val createResponse = client.post("/orders") {
            contentType(ContentType.Application.Json)
            bearerAuth(token)
            setBody("""{"items":[{"productId":1,"quantity":1}]}""")
        }
        assertEquals(HttpStatusCode.Created, createResponse.status)

        val listResponse = client.get("/orders") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.OK, listResponse.status)

        val cancelResponse = client.delete("/orders/101") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.NoContent, cancelResponse.status)
    }
}
