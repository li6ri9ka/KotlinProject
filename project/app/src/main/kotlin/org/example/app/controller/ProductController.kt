package org.example.app.controller

import org.example.common.dto.product.ProductResponse
import org.example.domain.service.ProductService

class ProductController(
    private val productService: ProductService
) {
    fun listProducts(): List<ProductResponse> {
        // Will be implemented in the next commit of this stage.
        return emptyList()
    }

    fun getProduct(productId: Long): ProductResponse {
        // Will be implemented in the next commit of this stage.
        throw NotImplementedError("Product retrieval is not implemented yet")
    }
}
