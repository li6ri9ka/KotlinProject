package org.example.app.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.exception
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.path
import io.ktor.server.response.respond
import org.example.common.dto.ApiError
import org.example.domain.error.AccessDeniedException
import org.example.domain.error.AuthenticationFailedException
import org.example.domain.error.NotFoundException
import org.example.domain.error.ValidationException

fun Application.configureErrorHandling() {
    install(StatusPages) {
        exception<ValidationException> { call, ex ->
            call.application.environment.log.warn("Validation error on ${call.request.local.method.value} ${call.request.path()}: ${ex.message}")
            call.respond(HttpStatusCode.BadRequest, ApiError(code = "VALIDATION_ERROR", message = ex.message ?: "Validation error"))
        }
        exception<AuthenticationFailedException> { call, ex ->
            call.application.environment.log.warn("Authentication error on ${call.request.local.method.value} ${call.request.path()}: ${ex.message}")
            call.respond(HttpStatusCode.Unauthorized, ApiError(code = "AUTH_ERROR", message = ex.message ?: "Unauthorized"))
        }
        exception<AccessDeniedException> { call, ex ->
            call.application.environment.log.warn("Access denied on ${call.request.local.method.value} ${call.request.path()}: ${ex.message}")
            call.respond(HttpStatusCode.Forbidden, ApiError(code = "ACCESS_DENIED", message = ex.message ?: "Forbidden"))
        }
        exception<NotFoundException> { call, ex ->
            call.application.environment.log.info("Not found on ${call.request.local.method.value} ${call.request.path()}: ${ex.message}")
            call.respond(HttpStatusCode.NotFound, ApiError(code = "NOT_FOUND", message = ex.message ?: "Not found"))
        }
        exception<Throwable> { call, ex ->
            call.application.environment.log.error(
                "Unhandled error on ${call.request.local.method.value} ${call.request.path()}: ${ex.message}",
                ex
            )
            call.respond(HttpStatusCode.InternalServerError, ApiError(code = "INTERNAL_ERROR", message = "Internal server error"))
        }
    }
}
