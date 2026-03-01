package org.example.domain.service

interface StatsService {
    fun getOrdersStats(): Map<String, Any>
}
