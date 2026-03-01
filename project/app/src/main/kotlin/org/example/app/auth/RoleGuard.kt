package org.example.app.auth

import org.example.app.principal.CurrentUser
import org.example.domain.error.AccessDeniedException
import org.example.domain.model.Role

fun CurrentUser.requireAdmin() {
    if (role != Role.ADMIN) {
        throw AccessDeniedException("Admin role required")
    }
}
