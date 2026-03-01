package org.example.app.controller

import org.example.common.dto.auth.LoginRequest
import org.example.common.dto.auth.RegisterRequest
import org.example.domain.model.Role
import org.example.domain.model.User
import org.example.domain.service.AuthService
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthControllerTest {
    @Test
    fun `register calls register and login then returns token`() {
        val calls = mutableListOf<String>()
        val service = object : AuthService {
            override fun register(email: String, password: String): User {
                calls += "register:$email"
                return User(
                    id = 1L,
                    email = email,
                    passwordHash = "hash",
                    role = Role.USER
                )
            }

            override fun login(email: String, password: String): String {
                calls += "login:$email"
                return "token-123"
            }
        }

        val controller = AuthController(service)
        val response = controller.register(RegisterRequest("user@example.com", "strongPass123"))

        assertEquals("token-123", response.accessToken)
        assertEquals(listOf("register:user@example.com", "login:user@example.com"), calls)
    }

    @Test
    fun `login returns token from service`() {
        val service = object : AuthService {
            override fun register(email: String, password: String): User = error("unused")
            override fun login(email: String, password: String): String = "jwt-token"
        }

        val controller = AuthController(service)
        val response = controller.login(LoginRequest("user@example.com", "strongPass123"))

        assertEquals("jwt-token", response.accessToken)
    }
}
