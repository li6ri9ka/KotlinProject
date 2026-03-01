package org.example.domain.service

import org.example.domain.model.User

interface AuthService {
    fun register(email: String, password: String): User
    fun login(email: String, password: String): String
}
