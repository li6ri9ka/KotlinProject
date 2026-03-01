package org.example.domain.service

import org.example.domain.model.Product

interface ProductService {
    fun listProducts(): List<Product>
    fun getProduct(productId: Long): Product
    fun createProduct(product: Product): Product
    fun updateProduct(product: Product): Product
    fun deleteProduct(productId: Long)
}
