package org.example.app.principal

import io.ktor.server.auth.jwt.JWTPrincipal
import org.example.app.auth.JwtClaims
import org.example.domain.model.Role

fun JWTPrincipal.toCurrentUser(): CurrentUser? {
    val id = payload.subject?.toLongOrNull() ?: return null
    val role = payload.getClaim(JwtClaims.ROLE).asString()?.let { runCatching { Role.valueOf(it) }.getOrNull() } ?: return null
    val email = payload.getClaim(JwtClaims.EMAIL).asString()
    return CurrentUser(id = id, role = role, email = email)
}
