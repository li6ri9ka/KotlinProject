package org.example.app.config

import org.example.app.auth.JwtConfig
import org.example.data.cache.RedisConfig
import org.example.data.db.config.DatabaseConfig

object AppEnvironment {
    fun dbConfig(): DatabaseConfig = DatabaseConfig(
        jdbcUrl = env("DB_JDBC_URL", "jdbc:postgresql://localhost:5432/shop_db"),
        username = env("DB_USERNAME", "shop"),
        password = env("DB_PASSWORD", "shop"),
        runMigrations = env("DB_RUN_MIGRATIONS", "true").toBoolean()
    )

    fun jwtConfig(): JwtConfig = JwtConfig(
        secret = env("JWT_SECRET", "dev-secret"),
        issuer = env("JWT_ISSUER", "shop-service"),
        audience = env("JWT_AUDIENCE", "shop-clients"),
        realm = env("JWT_REALM", "shop-api"),
        expiresInSeconds = env("JWT_EXPIRES_IN_SECONDS", "3600").toLong()
    )

    fun redisConfig(): RedisConfig = RedisConfig(
        host = env("REDIS_HOST", "localhost"),
        port = env("REDIS_PORT", "6379").toInt(),
        password = envOptional("REDIS_PASSWORD"),
        enabled = env("REDIS_ENABLED", "false").toBoolean(),
        productTtlSeconds = env("REDIS_PRODUCT_TTL_SECONDS", "300").toLong(),
        orderTtlSeconds = env("REDIS_ORDER_TTL_SECONDS", "120").toLong()
    )

    private fun env(name: String, defaultValue: String): String =
        System.getenv(name)?.takeIf { it.isNotBlank() } ?: defaultValue

    private fun envOptional(name: String): String? =
        System.getenv(name)?.takeIf { it.isNotBlank() }
}
