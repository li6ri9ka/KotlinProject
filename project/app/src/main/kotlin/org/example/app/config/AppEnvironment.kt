package org.example.app.config

import org.example.data.db.config.DatabaseConfig

object AppEnvironment {
    fun dbConfig(): DatabaseConfig = DatabaseConfig(
        jdbcUrl = env("DB_JDBC_URL", "jdbc:postgresql://localhost:5432/shop_db"),
        username = env("DB_USERNAME", "shop"),
        password = env("DB_PASSWORD", "shop"),
        runMigrations = env("DB_RUN_MIGRATIONS", "true").toBoolean()
    )

    private fun env(name: String, defaultValue: String): String =
        System.getenv(name)?.takeIf { it.isNotBlank() } ?: defaultValue
}
