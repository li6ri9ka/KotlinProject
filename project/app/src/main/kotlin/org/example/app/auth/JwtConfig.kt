package org.example.app.auth

data class JwtConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val realm: String
)
