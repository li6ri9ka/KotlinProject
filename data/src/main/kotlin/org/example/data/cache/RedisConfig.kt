package org.example.data.cache

data class RedisConfig(
    val host: String,
    val port: Int,
    val password: String? = null,
    val enabled: Boolean = true,
    val productTtlSeconds: Long = 300,
    val orderTtlSeconds: Long = 120
)
