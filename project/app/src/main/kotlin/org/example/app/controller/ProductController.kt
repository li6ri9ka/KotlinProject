package org.example.app.controller

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.example.common.dto.product.ProductResponse
import org.example.data.cache.CacheFacade
import org.example.data.cache.CacheKeys
import org.example.domain.service.ProductService

class ProductController(
    private val productService: ProductService,
    private val cacheFacade: CacheFacade,
    private val cacheTtlSeconds: Long
) {
    private val json = Json { ignoreUnknownKeys = true }

    fun listProducts(): List<ProductResponse> {
        cacheFacade.get(CacheKeys.PRODUCTS_ALL)?.let { cached ->
            runCatching {
                return json.decodeFromString(ListSerializer(ProductResponse.serializer()), cached)
            }
        }

        val products = productService.listProducts().map { product ->
            ProductResponse(
                id = product.id,
                name = product.name,
                description = product.description,
                price = product.price.toPlainString(),
                stock = product.stock
            )
        }

        cacheFacade.set(
            CacheKeys.PRODUCTS_ALL,
            json.encodeToString(ListSerializer(ProductResponse.serializer()), products),
            cacheTtlSeconds
        )

        products.forEach { product ->
            cacheFacade.set(
                CacheKeys.productById(product.id),
                json.encodeToString(ProductResponse.serializer(), product),
                cacheTtlSeconds
            )
        }

        return products
    }

    fun getProduct(productId: Long): ProductResponse {
        cacheFacade.get(CacheKeys.productById(productId))?.let { cached ->
            runCatching {
                return json.decodeFromString(ProductResponse.serializer(), cached)
            }
        }

        val product = productService.getProduct(productId)
        val response = ProductResponse(
            id = product.id,
            name = product.name,
            description = product.description,
            price = product.price.toPlainString(),
            stock = product.stock
        )

        cacheFacade.set(
            CacheKeys.productById(productId),
            json.encodeToString(ProductResponse.serializer(), response),
            cacheTtlSeconds
        )

        return response
    }
}
