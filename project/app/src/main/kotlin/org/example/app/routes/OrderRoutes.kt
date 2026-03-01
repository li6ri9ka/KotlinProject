package org.example.app.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.example.app.auth.AUTH_JWT
import org.example.common.dto.order.CreateOrderRequest

fun Application.configureOrderRoutes() {
    routing {
        authenticate(AUTH_JWT) {
            route(ApiRoutes.ORDERS) {
                post {
                    call.receive<CreateOrderRequest>()
                    call.respond(HttpStatusCode.NotImplemented)
                }
                get {
                    call.respond(HttpStatusCode.NotImplemented)
                }
                delete(ApiRoutes.ORDER_ID) {
                    call.respond(HttpStatusCode.NotImplemented)
                }
            }
        }
    }
}
