package org.example.app.principal

import kotlinx.serialization.Serializable
import org.example.domain.model.Role

@Serializable
data class CurrentUser(
    val id: Long,
    val email: String,
    val role: Role
)
