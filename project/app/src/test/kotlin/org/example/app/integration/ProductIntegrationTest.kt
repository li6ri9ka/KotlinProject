package org.example.app.integration

import org.example.app.testsupport.IntegrationContainers
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class ProductIntegrationTest : IntegrationContainers() {
    @Test
    @Disabled("Integration flow scaffold. Will be implemented in next test stage commit.")
    fun `get products with postgres and redis`() {
        // Arrange containers + seed DB
        // Act call /products
        // Assert HTTP 200 and payload
    }
}
