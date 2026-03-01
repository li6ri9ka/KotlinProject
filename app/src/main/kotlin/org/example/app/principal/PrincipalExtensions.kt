package org.example.app.principal

import io.ktor.server.auth.jwt.JWTPrincipal
import org.example.app.auth.JwtTokenService
import org.example.domain.model.Role

fun JWTPrincipal.toCurrentUser(): CurrentUser? {
    val id = payload.subject?.toLongOrNull() ?: return null
    val email = payload.getClaim(JwtTokenService.CLAIM_EMAIL).asString() ?: return null
    val roleRaw = payload.getClaim(JwtTokenService.CLAIM_ROLE).asString() ?: return null
    val role = runCatching { Role.valueOf(roleRaw) }.getOrNull() ?: return null
    return CurrentUser(id = id, email = email, role = role)
}
