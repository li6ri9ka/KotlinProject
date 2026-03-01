package org.example.data.queue

import org.example.common.dto.event.OrderEventMessage

object NoOpOrderEventPublisher : OrderEventPublisher {
    override fun publish(event: OrderEventMessage) = Unit
}
