package org.example.app.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.jwt

const val AUTH_JWT = "auth-jwt"

fun Application.configureSecurity(jwtConfig: JwtConfig) {
    check(jwtConfig.secret.isNotBlank()) { "JWT secret must not be blank" }

    install(Authentication) {
        jwt(AUTH_JWT) {
            realm = jwtConfig.realm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtConfig.secret))
                    .withIssuer(jwtConfig.issuer)
                    .withAudience(jwtConfig.audience)
                    .build()
            )
            validate { credential ->
                val email = credential.payload.getClaim(JwtTokenService.CLAIM_EMAIL).asString()
                val role = credential.payload.getClaim(JwtTokenService.CLAIM_ROLE).asString()
                if (!email.isNullOrBlank() && !role.isNullOrBlank()) {
                    io.ktor.server.auth.jwt.JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}
