package org.example.app.principal

import io.ktor.server.auth.jwt.JWTPrincipal
import org.example.app.auth.CLAIM_EMAIL
import org.example.app.auth.CLAIM_ROLE
import org.example.domain.model.Role

fun JWTPrincipal.toCurrentUser(): CurrentUser? {
    val id = payload.subject?.toLongOrNull() ?: return null
    val role = payload.getClaim(CLAIM_ROLE).asString()?.let { runCatching { Role.valueOf(it) }.getOrNull() } ?: return null
    val email = payload.getClaim(CLAIM_EMAIL).asString()
    return CurrentUser(id = id, email = email, role = role)
}
