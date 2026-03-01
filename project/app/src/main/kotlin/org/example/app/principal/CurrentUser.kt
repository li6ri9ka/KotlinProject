package org.example.app.principal

import org.example.domain.model.Role

data class CurrentUser(
    val id: Long,
    val email: String,
    val role: Role
)
