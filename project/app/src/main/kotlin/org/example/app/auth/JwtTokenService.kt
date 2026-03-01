package org.example.app.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.example.domain.model.Role
import org.example.domain.service.auth.TokenService
import java.time.Clock
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date

class JwtTokenService(
    private val jwtConfig: JwtConfig,
    private val clock: Clock = Clock.systemUTC()
) : TokenService {
    override fun issueToken(userId: Long, email: String, role: Role): String {
        val now = Instant.now(clock)
        val expiresAt = now.plus(jwtConfig.expiresInSeconds, ChronoUnit.SECONDS)

        return JWT.create()
            .withIssuer(jwtConfig.issuer)
            .withAudience(jwtConfig.audience)
            .withSubject(userId.toString())
            .withClaim(CLAIM_EMAIL, email)
            .withClaim(CLAIM_ROLE, role.name)
            .withIssuedAt(Date.from(now))
            .withExpiresAt(Date.from(expiresAt))
            .sign(Algorithm.HMAC256(jwtConfig.secret))
    }

    companion object {
        const val CLAIM_EMAIL = "email"
        const val CLAIM_ROLE = "role"
    }
}
