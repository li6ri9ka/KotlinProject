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
import org.example.app.auth.AUTH_JWT
import org.example.app.auth.requireAdmin
import org.example.app.controller.AdminController
import org.example.app.principal.toCurrentUser
import org.example.common.dto.product.CreateProductRequest
import org.example.common.dto.product.UpdateProductRequest
import org.example.domain.error.AccessDeniedException
import org.example.domain.error.ValidationException

fun Application.configureAdminRoutes(adminController: AdminController) {
    routing {
        authenticate(AUTH_JWT) {
            route(ApiRoutes.PRODUCTS) {
                post {
                    call.requireAdmin()
                    val request = call.receive<CreateProductRequest>()
                    val created = adminController.createProduct(request)
                    call.respond(HttpStatusCode.Created, created)
                }

                put(ApiRoutes.PRODUCT_ID) {
                    call.requireAdmin()
                    val productId = call.parameters["id"]?.toLongOrNull()
                        ?: throw ValidationException("Invalid product id")
                    val request = call.receive<UpdateProductRequest>()
                    val updated = adminController.updateProduct(productId, request)
                    call.respond(HttpStatusCode.OK, updated)
                }

                delete(ApiRoutes.PRODUCT_ID) {
                    call.requireAdmin()
                    val productId = call.parameters["id"]?.toLongOrNull()
                        ?: throw ValidationException("Invalid product id")
                    adminController.deleteProduct(productId)
                    call.respond(HttpStatusCode.NoContent)
                }
            }

            route(ApiRoutes.STATS) {
                get(ApiRoutes.STATS_ORDERS) {
                    call.requireAdmin()
                    call.respond(HttpStatusCode.OK, adminController.orderStats())
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
