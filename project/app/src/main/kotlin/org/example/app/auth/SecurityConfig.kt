package org.example.app.auth

import io.ktor.server.application.Application

fun Application.configureSecurity(jwtConfig: JwtConfig) {
    // JWT auth setup will be implemented in auth implementation step.
    check(jwtConfig.secret.isNotBlank()) { "JWT secret must not be blank" }
}
