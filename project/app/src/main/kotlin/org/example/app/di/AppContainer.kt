package org.example.app.di

import org.example.app.auth.BCryptPasswordHasher
import org.example.app.auth.JwtConfig
import org.example.app.auth.JwtTokenService
import org.example.app.controller.AuthController
import org.example.data.repository.impl.ExposedUserRepository
import org.example.data.service.impl.AuthServiceImpl
import java.time.Clock

class AppContainer(
    jwtConfig: JwtConfig
) {
    private val userRepository = ExposedUserRepository(Clock.systemUTC())
    private val passwordHasher = BCryptPasswordHasher()
    private val tokenService = JwtTokenService(jwtConfig = jwtConfig, clock = Clock.systemUTC())
    private val authService = AuthServiceImpl(
        userRepository = userRepository,
        passwordHasher = passwordHasher,
        tokenService = tokenService
    )

    val authController: AuthController = AuthController(authService)
}
