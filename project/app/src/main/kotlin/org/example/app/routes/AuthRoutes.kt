package org.example.app.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.example.app.auth.AUTH_JWT
import org.example.app.controller.AuthController
import org.example.app.principal.toCurrentUser
import org.example.common.dto.ApiError
import org.example.common.dto.auth.LoginRequest
import org.example.common.dto.auth.RegisterRequest

fun Application.configureAuthRoutes(authController: AuthController) {
    routing {
        route(ApiRoutes.AUTH) {
            post(ApiRoutes.REGISTER) {
                val request = call.receive<RegisterRequest>()
                val response = authController.register(request)
                call.respond(HttpStatusCode.Created, response)
            }

            post(ApiRoutes.LOGIN) {
                val request = call.receive<LoginRequest>()
                val response = authController.login(request)
                call.respond(HttpStatusCode.OK, response)
            }

            authenticate(AUTH_JWT) {
                get(ApiRoutes.ME) {
                    val principal = call.principal<JWTPrincipal>()?.toCurrentUser()
                    if (principal == null) {
                        call.respond(HttpStatusCode.Unauthorized, ApiError("AUTH_ERROR", "Invalid token"))
                        return@get
                    }
                    call.respond(principal)
                }
            }
        }
    }
}
