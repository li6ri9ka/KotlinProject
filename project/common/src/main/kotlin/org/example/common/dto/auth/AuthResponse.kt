package org.example.common.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val accessToken: String,
    val tokenType: String = "Bearer"
)
