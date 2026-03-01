package org.example.app.bootstrap

import org.example.app.auth.BCryptPasswordHasher
import org.example.app.config.AdminBootstrapConfig
import org.example.domain.model.Role
import org.example.domain.model.User
import org.example.domain.repository.UserRepository

class AdminBootstrapper(
    private val userRepository: UserRepository,
    private val passwordHasher: BCryptPasswordHasher
) {
    fun ensureAdmin(config: AdminBootstrapConfig) {
        if (!config.enabled) return

        val email = config.email!!.trim().lowercase()
        val password = config.password!!

        val existing = userRepository.findByEmail(email)
        if (existing != null) {
            if (existing.role != Role.ADMIN) {
                println("Admin bootstrap skipped: user $email exists with role ${existing.role}")
            }
            return
        }

        userRepository.create(
            User(
                id = 0,
                email = email,
                passwordHash = passwordHasher.hash(password),
                role = Role.ADMIN
            )
        )
        println("Admin user bootstrapped: $email")
    }
}
