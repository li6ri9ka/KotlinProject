package org.example.app.e2e

import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.example.app.auth.JwtConfig
import org.example.app.auth.JwtTokenService
import org.example.app.auth.configureSecurity
import org.example.app.controller.AuthController
import org.example.app.plugins.configureSerialization
import org.example.app.routes.configureAuthRoutes
import org.example.domain.model.Role
import org.example.domain.model.User
import org.example.domain.service.AuthService
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AuthE2ETest {
    @Test
    fun `register and login through API`() = testApplication {
        val jwtConfig = JwtConfig(
            secret = "test-secret",
            issuer = "shop-service",
            audience = "shop-clients",
            realm = "shop-api",
            expiresInSeconds = 3600
        )
        val tokenService = JwtTokenService(jwtConfig)

        val authService = object : AuthService {
            override fun register(email: String, password: String): User =
                User(id = 1, email = email, passwordHash = "hash", role = Role.USER)

            override fun login(email: String, password: String): String =
                tokenService.issueToken(userId = 1, email = email, role = Role.USER)
        }

        application {
            configureSerialization()
            configureSecurity(jwtConfig)
            configureAuthRoutes(AuthController(authService))
        }

        val registerResponse = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"user@example.com","password":"strongPass123"}""")
        }
        assertEquals(HttpStatusCode.Created, registerResponse.status)

        val loginResponse = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"user@example.com","password":"strongPass123"}""")
        }
        assertEquals(HttpStatusCode.OK, loginResponse.status)

        val token = Json.parseToJsonElement(loginResponse.bodyAsText())
            .jsonObject["accessToken"]
            ?.jsonPrimitive
            ?.content
            ?: error("No accessToken in response")
        kotlin.test.assertTrue(token.isNotBlank())
    }
}
