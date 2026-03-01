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
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.example.app.auth.AUTH_JWT
import org.example.app.controller.OrderController
import org.example.app.principal.toCurrentUser
import org.example.common.dto.order.CreateOrderRequest
import org.example.domain.error.AuthenticationFailedException
import org.example.domain.error.ValidationException

fun Application.configureOrderRoutes(orderController: OrderController) {
    routing {
        authenticate(AUTH_JWT) {
            route(ApiRoutes.ORDERS) {
                post {
                    val userId = call.currentUserId()
                    val request = call.receive<CreateOrderRequest>()
                    val response = orderController.createOrder(userId, request)
                    call.respond(HttpStatusCode.Created, response)
                }

                get {
                    val userId = call.currentUserId()
                    call.respond(HttpStatusCode.OK, orderController.getOrders(userId))
                }

                delete(ApiRoutes.ORDER_ID) {
                    val userId = call.currentUserId()
                    val orderId = call.parameters["id"]?.toLongOrNull()
                        ?: throw ValidationException("Invalid order id")
                    orderController.cancelOrder(userId, orderId)
                    call.respond(HttpStatusCode.NoContent)
                }
            }
        }
    }
}

private fun io.ktor.server.application.ApplicationCall.currentUserId(): Long {
    val principal = principal<JWTPrincipal>()?.toCurrentUser()
        ?: throw AuthenticationFailedException("Invalid token")
    return principal.id
}
