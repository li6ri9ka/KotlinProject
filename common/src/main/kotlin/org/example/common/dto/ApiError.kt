package org.example.common.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiError(
    val code: String,
    val message: String
)
