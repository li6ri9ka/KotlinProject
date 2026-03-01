package org.example.app.controller

import org.example.common.dto.product.ProductResponse
import org.example.domain.service.ProductService

class ProductController(
    private val productService: ProductService
) {
    fun listProducts(): List<ProductResponse> =
        productService.listProducts().map { product ->
            ProductResponse(
                id = product.id,
                name = product.name,
                description = product.description,
                price = product.price.toPlainString(),
                stock = product.stock
            )
        }

    fun getProduct(productId: Long): ProductResponse {
        val product = productService.getProduct(productId)
        return ProductResponse(
            id = product.id,
            name = product.name,
            description = product.description,
            price = product.price.toPlainString(),
            stock = product.stock
        )
    }
}
