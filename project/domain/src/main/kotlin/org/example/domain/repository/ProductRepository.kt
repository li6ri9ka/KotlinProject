package org.example.domain.repository

import org.example.domain.model.Product

interface ProductRepository {
    fun findAll(): List<Product>
    fun findById(id: Long): Product?
    fun create(product: Product): Product
    fun update(product: Product): Product
    fun delete(productId: Long)
}
