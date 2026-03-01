package org.example.data.queue

import com.rabbitmq.client.ConnectionFactory
import org.example.common.dto.event.OrderEventMessage
import kotlinx.serialization.json.Json

class RabbitMqOrderEventPublisher(
    private val config: RabbitMqConfig
) : OrderEventPublisher {
    private val json = Json { ignoreUnknownKeys = true }

    override fun publish(event: OrderEventMessage) {
        val factory = ConnectionFactory().apply {
            host = config.host
            port = config.port
            username = config.username
            password = config.password
            virtualHost = config.virtualHost
        }

        factory.newConnection().use { connection ->
            connection.createChannel().use { channel ->
                channel.queueDeclare(config.queueName, true, false, false, null)
                val body = json.encodeToString(OrderEventMessage.serializer(), event)
                channel.basicPublish("", config.queueName, null, body.toByteArray(Charsets.UTF_8))
            }
        }
    }
}
