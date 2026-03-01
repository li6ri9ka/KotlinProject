package org.example.app.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureProductRoutes() {
    routing {
        route(ApiRoutes.PRODUCTS) {
            get {
                call.respond(HttpStatusCode.NotImplemented)
            }
            get(ApiRoutes.PRODUCT_ID) {
                call.respond(HttpStatusCode.NotImplemented)
            }
        }
    }
}
