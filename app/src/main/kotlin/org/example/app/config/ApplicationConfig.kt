package org.example.app.config

data class ApplicationConfig(
    val appName: String = "shop-service"
)

data class AdminBootstrapConfig(
    val email: String?,
    val password: String?
) {
    val enabled: Boolean
        get() = !email.isNullOrBlank() && !password.isNullOrBlank()
}
