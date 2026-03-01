package org.example.data.cache

object NoOpCacheFacade : CacheFacade {
    override fun get(key: String): String? = null

    override fun set(key: String, value: String, ttlSeconds: Long) = Unit

    override fun invalidate(key: String) = Unit
}
