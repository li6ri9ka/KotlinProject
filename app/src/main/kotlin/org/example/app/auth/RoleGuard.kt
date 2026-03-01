package org.example.app.auth

import org.example.app.principal.CurrentUser
import org.example.domain.error.AccessDeniedException
import org.example.domain.model.Role

fun CurrentUser.requireRole(expected: Role) {
    if (role != expected) {
        throw AccessDeniedException("Required role: ${expected.name}")
    }
}

fun CurrentUser.requireAdmin() {
    requireRole(Role.ADMIN)
}
