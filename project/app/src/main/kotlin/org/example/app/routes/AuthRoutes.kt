package org.example.app.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.example.common.dto.auth.LoginRequest
import org.example.common.dto.auth.RegisterRequest

fun Application.configureAuthRoutes() {
    routing {
        route(ApiRoutes.AUTH) {
            post(ApiRoutes.REGISTER) {
                call.receive<RegisterRequest>()
                call.respond(HttpStatusCode.NotImplemented)
            }
            post(ApiRoutes.LOGIN) {
                call.receive<LoginRequest>()
                call.respond(HttpStatusCode.NotImplemented)
            }
        }
    }
}
