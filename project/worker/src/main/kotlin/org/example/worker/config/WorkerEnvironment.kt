package org.example.worker.config

import org.example.data.queue.RabbitMqConfig

object WorkerEnvironment {
    fun rabbitMqConfig(): RabbitMqConfig = RabbitMqConfig(
        host = env("RABBITMQ_HOST", "localhost"),
        port = env("RABBITMQ_PORT", "5672").toInt(),
        username = env("RABBITMQ_USERNAME", "guest"),
        password = env("RABBITMQ_PASSWORD", "guest"),
        virtualHost = env("RABBITMQ_VHOST", "/"),
        enabled = env("RABBITMQ_ENABLED", "false").toBoolean()
    )

    private fun env(name: String, defaultValue: String): String =
        System.getenv(name)?.takeIf { it.isNotBlank() } ?: defaultValue
}
