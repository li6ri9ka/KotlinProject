package org.example.data.queue

import org.example.common.dto.event.OrderEventMessage

interface OrderEventPublisher {
    fun publish(event: OrderEventMessage)
}
