package org.example.app.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.jwt

const val AUTH_JWT = "auth-jwt"
const val CLAIM_ROLE = "role"
const val CLAIM_EMAIL = "email"

fun Application.configureSecurity(jwtConfig: JwtConfig) {
    install(Authentication) {
        jwt(AUTH_JWT) {
            realm = jwtConfig.realm
            verifier(
                JWT.require(Algorithm.HMAC256(jwtConfig.secret))
                    .withIssuer(jwtConfig.issuer)
                    .withAudience(jwtConfig.audience)
                    .build()
            )
            validate { credential ->
                val subject = credential.payload.subject
                val role = credential.payload.getClaim(CLAIM_ROLE).asString()
                if (!subject.isNullOrBlank() && !role.isNullOrBlank()) {
                    io.ktor.server.auth.jwt.JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}
