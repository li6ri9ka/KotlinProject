package org.example.app.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.example.app.auth.requireAdmin
import org.example.app.controller.AdminController
import org.example.app.principal.toCurrentUser
import org.example.common.dto.product.CreateProductRequest
import org.example.common.dto.product.UpdateProductRequest
import org.example.domain.error.AccessDeniedException
import org.example.domain.error.ValidationException

private const val AUTH_JWT = "auth-jwt"

fun Application.configureAdminRoutes(adminController: AdminController) {
    routing {
        authenticate(AUTH_JWT) {
            route(ApiRoutes.PRODUCTS) {
                post {
                    call.requireAdmin()
                    call.receive<CreateProductRequest>()
                    call.respond(HttpStatusCode.NotImplemented)
                }

                put(ApiRoutes.PRODUCT_ID) {
                    call.requireAdmin()
                    call.parameters["id"]?.toLongOrNull()
                        ?: throw ValidationException("Invalid product id")
                    call.receive<UpdateProductRequest>()
                    call.respond(HttpStatusCode.NotImplemented)
                }

                delete(ApiRoutes.PRODUCT_ID) {
                    call.requireAdmin()
                    call.parameters["id"]?.toLongOrNull()
                        ?: throw ValidationException("Invalid product id")
                    call.respond(HttpStatusCode.NotImplemented)
                }
            }

            route(ApiRoutes.STATS) {
                get(ApiRoutes.STATS_ORDERS) {
                    call.requireAdmin()
                    call.respond(HttpStatusCode.NotImplemented)
                }
            }
        }
    }
}

private fun io.ktor.server.application.ApplicationCall.requireAdmin() {
    val currentUser = principal<JWTPrincipal>()?.toCurrentUser()
        ?: throw AccessDeniedException("Invalid token")
    currentUser.requireAdmin()
}
