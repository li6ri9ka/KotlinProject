package org.example.app.integration

import org.example.app.testsupport.IntegrationContainers
import org.example.data.db.tables.ProductsTable
import org.example.data.repository.impl.ExposedProductRepository
import org.example.domain.model.Product
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ProductIntegrationTest : IntegrationContainers() {
    private val repo = ExposedProductRepository()

    @BeforeEach
    fun cleanup() {
        transaction {
            ProductsTable.deleteAll()
        }
    }

    @Test
    fun `create and find product via postgres`() {
        val created = repo.create(
            Product(
                id = 0,
                name = "Phone",
                description = "Smartphone",
                price = BigDecimal("199.99"),
                stock = 10
            )
        )

        val found = repo.findById(created.id)
        assertNotNull(found)
        assertEquals("Phone", found.name)
        assertEquals(BigDecimal("199.99"), found.price)
        assertEquals(10, found.stock)
    }
}
