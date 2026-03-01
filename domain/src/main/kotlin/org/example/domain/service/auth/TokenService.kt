package org.example.domain.service.auth

import org.example.domain.model.Role

interface TokenService {
    fun issueToken(userId: Long, email: String, role: Role): String
}
