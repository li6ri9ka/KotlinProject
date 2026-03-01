package org.example.data.db.config

data class DatabaseConfig(
    val jdbcUrl: String,
    val username: String,
    val password: String,
    val driverClassName: String = "org.postgresql.Driver",
    val maximumPoolSize: Int = 10,
    val runMigrations: Boolean = true
)
