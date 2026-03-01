package org.example.data.cache

interface CacheFacade {
    fun get(key: String): String?
    fun set(key: String, value: String, ttlSeconds: Long)
    fun invalidate(key: String)
}
