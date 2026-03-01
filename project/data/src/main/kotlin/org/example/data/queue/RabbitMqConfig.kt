package org.example.data.queue

data class RabbitMqConfig(
    val host: String,
    val port: Int,
    val username: String,
    val password: String,
    val virtualHost: String = "/",
    val queueName: String = QueueNames.ORDER_EVENTS,
    val enabled: Boolean = false
)
