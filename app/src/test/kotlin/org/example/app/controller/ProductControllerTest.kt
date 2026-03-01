package org.example.app.controller

import org.example.data.cache.NoOpCacheFacade
import org.example.domain.model.Product
import org.example.domain.service.ProductService
import kotlin.test.Test
import kotlin.test.assertEquals
import java.math.BigDecimal

class ProductControllerTest {
    @Test
    fun `listProducts maps domain to dto`() {
        val service = object : ProductService {
            override fun listProducts(): List<Product> = listOf(
                Product(1, "Phone", "Smartphone", BigDecimal("199.99"), 7)
            )

            override fun getProduct(productId: Long): Product = error("unused")
            override fun createProduct(product: Product): Product = error("unused")
            override fun updateProduct(product: Product): Product = error("unused")
            override fun deleteProduct(productId: Long) = Unit
        }

        val controller = ProductController(
            productService = service,
            cacheFacade = NoOpCacheFacade,
            cacheTtlSeconds = 60
        )
        val result = controller.listProducts()

        assertEquals(1, result.size)
        assertEquals("Phone", result.first().name)
        assertEquals("199.99", result.first().price)
    }
}
