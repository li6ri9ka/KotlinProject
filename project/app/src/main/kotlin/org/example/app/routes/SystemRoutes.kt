package org.example.app.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureSystemRoutes() {
    routing {
        get(ApiRoutes.HEALTH) {
            call.respondText("OK", status = HttpStatusCode.OK)
        }
    }
}
