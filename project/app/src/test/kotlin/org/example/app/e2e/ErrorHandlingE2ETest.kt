package org.example.app.e2e

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.example.app.controller.ProductController
import org.example.app.plugins.configureErrorHandling
import org.example.app.plugins.configureSerialization
import org.example.app.routes.configureProductRoutes
import org.example.data.cache.NoOpCacheFacade
import org.example.domain.model.Product
import org.example.domain.service.ProductService
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ErrorHandlingE2ETest {
    @Test
    fun `invalid product id returns validation error response`() = testApplication {
        val service = object : ProductService {
            override fun listProducts(): List<Product> = emptyList()
            override fun getProduct(productId: Long): Product = error("unused")
            override fun createProduct(product: Product): Product = error("unused")
            override fun updateProduct(product: Product): Product = error("unused")
            override fun deleteProduct(productId: Long) = Unit
        }

        application {
            configureSerialization()
            configureErrorHandling()
            configureProductRoutes(
                ProductController(
                    productService = service,
                    cacheFacade = NoOpCacheFacade,
                    cacheTtlSeconds = 60
                )
            )
        }

        val response = client.get("/products/abc")
        assertEquals(HttpStatusCode.BadRequest, response.status)

        val body = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        assertEquals("VALIDATION_ERROR", body["code"]?.jsonPrimitive?.content)
    }

    @Test
    fun `unhandled exception returns internal error response`() = testApplication {
        application {
            configureSerialization()
            configureErrorHandling()
            routing {
                get("/boom") {
                    error("boom")
                }
            }
        }

        val response = client.get("/boom")
        assertEquals(HttpStatusCode.InternalServerError, response.status)

        val body = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        assertEquals("INTERNAL_ERROR", body["code"]?.jsonPrimitive?.content)
        assertEquals("Internal server error", body["message"]?.jsonPrimitive?.content)
    }
}
