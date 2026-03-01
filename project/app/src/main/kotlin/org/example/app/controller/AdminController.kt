package org.example.app.controller

import org.example.common.dto.admin.OrdersStatsResponse
import org.example.common.dto.product.CreateProductRequest
import org.example.common.dto.product.ProductResponse
import org.example.common.dto.product.UpdateProductRequest
import org.example.domain.error.ValidationException
import org.example.domain.model.Product
import org.example.domain.service.ProductService
import org.example.domain.service.StatsService
import java.math.BigDecimal

class AdminController(
    private val productService: ProductService,
    private val statsService: StatsService
) {
    fun createProduct(request: CreateProductRequest): ProductResponse {
        val created = productService.createProduct(request.toDomain(id = 0))
        return created.toResponse()
    }

    fun updateProduct(productId: Long, request: UpdateProductRequest): ProductResponse {
        val updated = productService.updateProduct(request.toDomain(id = productId))
        return updated.toResponse()
    }

    fun deleteProduct(productId: Long) {
        productService.deleteProduct(productId)
    }

    fun orderStats(): OrdersStatsResponse {
        val raw = statsService.getOrdersStats()
        val revenue = (raw["totalRevenue"] as? BigDecimal ?: BigDecimal.ZERO).toPlainString()

        return OrdersStatsResponse(
            totalOrders = (raw["totalOrders"] as? Long) ?: 0L,
            canceledOrders = (raw["canceledOrders"] as? Long) ?: 0L,
            createdOrders = (raw["createdOrders"] as? Long) ?: 0L,
            totalRevenue = revenue
        )
    }

    private fun CreateProductRequest.toDomain(id: Long): Product = Product(
        id = id,
        name = name,
        description = description,
        price = price.toBigDecimalOrNull() ?: throw ValidationException("Invalid price format"),
        stock = stock
    )

    private fun UpdateProductRequest.toDomain(id: Long): Product = Product(
        id = id,
        name = name,
        description = description,
        price = price.toBigDecimalOrNull() ?: throw ValidationException("Invalid price format"),
        stock = stock
    )

    private fun Product.toResponse(): ProductResponse = ProductResponse(
        id = id,
        name = name,
        description = description,
        price = price.toPlainString(),
        stock = stock
    )
}
