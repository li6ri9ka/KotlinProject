package org.example.data.cache

import redis.clients.jedis.DefaultJedisClientConfig
import redis.clients.jedis.HostAndPort
import redis.clients.jedis.JedisPooled

object RedisClientFactory {
    fun create(config: RedisConfig): JedisPooled {
        val builder = DefaultJedisClientConfig.builder()
        config.password?.let(builder::password)
        val clientConfig = builder.build()

        return JedisPooled(HostAndPort(config.host, config.port), clientConfig)
    }
}
