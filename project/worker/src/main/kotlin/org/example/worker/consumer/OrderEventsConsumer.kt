package org.example.worker.consumer

import com.rabbitmq.client.DeliverCallback
import com.rabbitmq.client.ConnectionFactory
import kotlinx.serialization.json.Json
import org.example.common.dto.event.OrderEventMessage
import org.example.data.queue.RabbitMqConfig
import org.example.worker.email.FakeEmailSender

class OrderEventsConsumer(
    private val config: RabbitMqConfig,
    private val emailSender: FakeEmailSender
) {
    private val json = Json { ignoreUnknownKeys = true }

    fun start() {
        if (!config.enabled) {
            return
        }

        val factory = ConnectionFactory().apply {
            host = config.host
            port = config.port
            username = config.username
            password = config.password
            virtualHost = config.virtualHost
        }

        val connection = factory.newConnection()
        val channel = connection.createChannel()

        channel.queueDeclare(config.queueName, true, false, false, null)

        val callback = DeliverCallback { _, delivery ->
            val body = delivery.body.toString(Charsets.UTF_8)
            runCatching {
                val event = json.decodeFromString(OrderEventMessage.serializer(), body)
                handle(event)
            }
        }

        channel.basicConsume(config.queueName, true, callback) { _ -> }
    }

    private fun handle(event: OrderEventMessage) {
        println("Received order event: type=${event.eventType} order=${event.orderId} user=${event.userId}")
        emailSender.send(
            to = "user-${event.userId}@example.com",
            subject = "Order event ${event.eventType}",
            body = "Order ${event.orderId}: ${event.payload}"
        )
    }
}
