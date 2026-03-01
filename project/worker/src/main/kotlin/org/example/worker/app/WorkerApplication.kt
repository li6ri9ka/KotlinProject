package org.example.worker.app

import org.example.worker.config.WorkerEnvironment
import org.example.worker.consumer.OrderEventsConsumer
import org.example.worker.email.FakeEmailSender

object WorkerApplication {
    @JvmStatic
    fun main(args: Array<String>) {
        val consumer = OrderEventsConsumer(
            config = WorkerEnvironment.rabbitMqConfig(),
            emailSender = FakeEmailSender()
        )
        consumer.start()
    }
}
