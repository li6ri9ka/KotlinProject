package org.example.common.utils

import java.time.Instant

fun interface ClockProvider {
    fun now(): Instant
}
