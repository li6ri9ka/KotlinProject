package org.example.data.service.impl

import org.example.domain.error.NotFoundException
import org.example.domain.error.ValidationException
import org.example.domain.model.Product
import org.example.domain.repository.ProductRepository
import org.example.domain.service.ProductService

class ProductServiceImpl(
    private val productRepository: ProductRepository
) : ProductService {
    override fun listProducts(): List<Product> = productRepository.findAll()

    override fun getProduct(productId: Long): Product =
        productRepository.findById(productId) ?: throw NotFoundException("Product not found: $productId")

    override fun createProduct(product: Product): Product {
        validate(product)
        return productRepository.create(product)
    }

    override fun updateProduct(product: Product): Product {
        validate(product)
        getProduct(product.id)
        return productRepository.update(product)
    }

    override fun deleteProduct(productId: Long) {
        getProduct(productId)
        productRepository.delete(productId)
    }

    private fun validate(product: Product) {
        if (product.name.isBlank()) throw ValidationException("Product name is required")
        if (product.price.signum() < 0) throw ValidationException("Product price must be >= 0")
        if (product.stock < 0) throw ValidationException("Product stock must be >= 0")
    }
}
