package org.example.domain.model

data class User(
    val id: Long,
    val email: String,
    val passwordHash: String,
    val role: Role
)
