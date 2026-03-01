package org.example.app.testsupport

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers(disabledWithoutDocker = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class IntegrationContainers {
    @Container
    val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:16")

    @Container
    val rabbitMq: RabbitMQContainer = RabbitMQContainer(DockerImageName.parse("rabbitmq:3.13-management"))

    @Container
    val redis: GenericContainer<*> = GenericContainer(DockerImageName.parse("redis:7")).withExposedPorts(6379)

    @BeforeAll
    fun setupEnv() {
        System.setProperty("DB_JDBC_URL", postgres.jdbcUrl)
        System.setProperty("DB_USERNAME", postgres.username)
        System.setProperty("DB_PASSWORD", postgres.password)

        System.setProperty("RABBITMQ_HOST", rabbitMq.host)
        System.setProperty("RABBITMQ_PORT", rabbitMq.amqpPort.toString())
        System.setProperty("RABBITMQ_USERNAME", rabbitMq.adminUsername)
        System.setProperty("RABBITMQ_PASSWORD", rabbitMq.adminPassword)

        System.setProperty("REDIS_HOST", redis.host)
        System.setProperty("REDIS_PORT", redis.getMappedPort(6379).toString())
    }
}
