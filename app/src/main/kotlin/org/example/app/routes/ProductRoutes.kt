package org.example.app.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.example.app.controller.ProductController
import org.example.domain.error.ValidationException

fun Application.configureProductRoutes(productController: ProductController) {
    routing {
        route(ApiRoutes.PRODUCTS) {
            get {
                call.respond(HttpStatusCode.OK, productController.listProducts())
            }

            get(ApiRoutes.PRODUCT_ID) {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: throw ValidationException("Invalid product id")
                call.respond(HttpStatusCode.OK, productController.getProduct(id))
            }
        }
    }
}
