package org.example.data.queue

import com.rabbitmq.client.ConnectionFactory
import kotlinx.serialization.json.Json
import org.example.common.dto.event.OrderEventMessage
import org.example.common.dto.event.OrderEventType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Testcontainers(disabledWithoutDocker = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RabbitMqOrderEventPublisherIntegrationTest {
    @Container
    private val rabbitMq = RabbitMQContainer("rabbitmq:3.13-management")

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `publish writes message to queue`() {
        val queueName = "order.events.test"
        val config = RabbitMqConfig(
            host = rabbitMq.host,
            port = rabbitMq.amqpPort,
            username = rabbitMq.adminUsername,
            password = rabbitMq.adminPassword,
            queueName = queueName,
            enabled = true
        )
        val publisher = RabbitMqOrderEventPublisher(config)

        val event = OrderEventMessage(
            eventType = OrderEventType.ORDER_CREATED,
            orderId = 55L,
            userId = 7L,
            createdAt = "2026-03-01T10:00:00Z",
            payload = "total=99.99"
        )

        publisher.publish(event)

        val factory = ConnectionFactory().apply {
            host = config.host
            port = config.port
            username = config.username
            password = config.password
            virtualHost = config.virtualHost
        }

        factory.newConnection().use { connection ->
            connection.createChannel().use { channel ->
                channel.queueDeclare(queueName, true, false, false, null)
                val delivery = channel.basicGet(queueName, true)
                assertNotNull(delivery)
                val body = delivery.body.toString(Charsets.UTF_8)
                val received = json.decodeFromString(OrderEventMessage.serializer(), body)
                assertEquals(event, received)
            }
        }
    }
}
