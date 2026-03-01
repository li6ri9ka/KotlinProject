package org.example.app.controller

import org.example.common.dto.auth.AuthResponse
import org.example.common.dto.auth.LoginRequest
import org.example.common.dto.auth.RegisterRequest
import org.example.domain.service.AuthService

class AuthController(
    private val authService: AuthService
) {
    fun register(request: RegisterRequest): AuthResponse {
        authService.register(request.email, request.password)
        val token = authService.login(request.email, request.password)
        return AuthResponse(accessToken = token)
    }

    fun login(request: LoginRequest): AuthResponse {
        val token = authService.login(request.email, request.password)
        return AuthResponse(accessToken = token)
    }
}
