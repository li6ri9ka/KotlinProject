package org.example.data.service.impl

import org.example.domain.error.AuthenticationFailedException
import org.example.domain.error.ValidationException
import org.example.domain.model.Role
import org.example.domain.model.User
import org.example.domain.repository.UserRepository
import org.example.domain.service.AuthService
import org.example.domain.service.auth.PasswordHasher
import org.example.domain.service.auth.TokenService

class AuthServiceImpl(
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher,
    private val tokenService: TokenService
) : AuthService {
    override fun register(email: String, password: String): User {
        validate(email, password)

        val normalizedEmail = email.trim().lowercase()
        if (userRepository.findByEmail(normalizedEmail) != null) {
            throw ValidationException("User with this email already exists")
        }

        val user = User(
            id = 0,
            email = normalizedEmail,
            passwordHash = passwordHasher.hash(password),
            role = Role.USER
        )

        return userRepository.create(user)
    }

    override fun login(email: String, password: String): String {
        val normalizedEmail = email.trim().lowercase()
        val user = userRepository.findByEmail(normalizedEmail)
            ?: throw AuthenticationFailedException("Invalid credentials")

        if (!passwordHasher.matches(password, user.passwordHash)) {
            throw AuthenticationFailedException("Invalid credentials")
        }

        return tokenService.issueToken(user.id, user.email, user.role)
    }

    private fun validate(email: String, password: String) {
        if (email.isBlank() || !email.contains("@")) {
            throw ValidationException("Invalid email")
        }
        if (password.length < 6) {
            throw ValidationException("Password must be at least 6 characters")
        }
    }
}
