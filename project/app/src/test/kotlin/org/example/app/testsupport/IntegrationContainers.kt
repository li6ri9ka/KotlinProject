package org.example.app.testsupport

import org.example.data.db.config.DatabaseConfig
import org.example.data.db.config.DatabaseFactory
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers(disabledWithoutDocker = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class IntegrationContainers {
    @Container
    val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:16")

    @BeforeAll
    fun initDb() {
        DatabaseFactory.init(
            DatabaseConfig(
                jdbcUrl = postgres.jdbcUrl,
                username = postgres.username,
                password = postgres.password,
                runMigrations = true
            )
        )
    }
}
