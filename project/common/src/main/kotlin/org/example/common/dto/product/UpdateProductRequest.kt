package org.example.common.dto.product

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProductRequest(
    val name: String,
    val description: String,
    val price: String,
    val stock: Int
)
