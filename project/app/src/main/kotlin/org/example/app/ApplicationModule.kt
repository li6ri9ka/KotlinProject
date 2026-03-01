package org.example.app

import io.ktor.server.application.Application
import org.example.app.auth.JwtConfig
import org.example.app.auth.configureSecurity
import org.example.app.routes.configureAuthRoutes

fun Application.module() {
    configureSecurity(
        JwtConfig(
            secret = "dev-secret",
            issuer = "shop-service",
            audience = "shop-clients",
            realm = "shop-api",
            expiresInSeconds = 3600
        )
    )
    configureAuthRoutes()
}
