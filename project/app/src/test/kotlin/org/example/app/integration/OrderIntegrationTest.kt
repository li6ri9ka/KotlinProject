package org.example.app.integration

import org.example.app.testsupport.IntegrationContainers
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class OrderIntegrationTest : IntegrationContainers() {
    @Test
    @Disabled("Integration flow scaffold. Will be implemented in next test stage commit.")
    fun `create order transactionally with postgres and queue`() {
        // Arrange auth + product stock
        // Act call /orders
        // Assert order persisted, stock decreased, event queued
    }
}
