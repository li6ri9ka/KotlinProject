package org.example.data.cache

import redis.clients.jedis.JedisPooled

class RedisCacheFacade(
    private val jedis: JedisPooled
) : CacheFacade {
    override fun get(key: String): String? = runCatching { jedis.get(key) }.getOrNull()

    override fun set(key: String, value: String, ttlSeconds: Long) {
        runCatching { jedis.setex(key, ttlSeconds, value) }
    }

    override fun invalidate(key: String) {
        runCatching { jedis.del(key) }
    }
}
