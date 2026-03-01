package org.example.data.repository.impl

import org.example.data.db.tables.ProductsTable
import org.example.domain.model.Product
import org.example.domain.repository.ProductRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.deleteWhere
import java.time.Clock
import java.time.OffsetDateTime

class ExposedProductRepository(
    private val clock: Clock = Clock.systemUTC()
) : ProductRepository {
    override fun findAll(): List<Product> = transaction {
        ProductsTable
            .selectAll()
            .map { it.toProduct() }
    }

    override fun findById(id: Long): Product? = transaction {
        ProductsTable
            .selectAll()
            .where { ProductsTable.id eq id }
            .limit(1)
            .firstOrNull()
            ?.toProduct()
    }

    override fun create(product: Product): Product = transaction {
        val now = OffsetDateTime.now(clock)
        val inserted = ProductsTable.insert {
            it[name] = product.name
            it[description] = product.description
            it[price] = product.price
            it[stock] = product.stock
            it[createdAt] = now
            it[updatedAt] = now
        }

        product.copy(id = inserted[ProductsTable.id].value)
    }

    override fun update(product: Product): Product = transaction {
        val updatedRows = ProductsTable.update({ ProductsTable.id eq product.id }) {
            it[name] = product.name
            it[description] = product.description
            it[price] = product.price
            it[stock] = product.stock
            it[updatedAt] = OffsetDateTime.now(clock)
        }

        if (updatedRows == 0) {
            throw IllegalStateException("Product with id=${product.id} not found")
        }

        product
    }

    override fun delete(productId: Long) {
        transaction {
            ProductsTable.deleteWhere { ProductsTable.id eq productId }
        }
    }

    private fun ResultRow.toProduct(): Product = Product(
        id = this[ProductsTable.id].value,
        name = this[ProductsTable.name],
        description = this[ProductsTable.description],
        price = this[ProductsTable.price],
        stock = this[ProductsTable.stock]
    )
}
