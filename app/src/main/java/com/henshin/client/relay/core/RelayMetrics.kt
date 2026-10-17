// Copyright 2026 ESC5P4/Kauri
// Licensed under the Apache License, Version 2.0. See LICENSE.

package com.henshin.client.relay.core

import java.util.concurrent.atomic.AtomicLong

/** Contadores y tiempos del relay, para medir en vez de adivinar. */
class RelayMetrics {

    private val received = AtomicLong()
    private val processed = AtomicLong()
    private val dropped = AtomicLong()
    private val failed = AtomicLong()
    private val totalNanos = AtomicLong()
    private val maxNanos = AtomicLong()

    fun onReceived() { received.incrementAndGet() }
    fun onDropped() { dropped.incrementAndGet() }
    fun onFailed() { failed.incrementAndGet() }

    fun onProcessed(nanos: Long) {
        processed.incrementAndGet()
        totalNanos.addAndGet(nanos)
        while (true) {
            val current = maxNanos.get()
            if (nanos <= current || maxNanos.compareAndSet(current, nanos)) break
        }
    }

    fun snapshot(): Snapshot {
        val done = processed.get()
        return Snapshot(
            received = received.get(),
            processed = done,
            dropped = dropped.get(),
            failed = failed.get(),
            avgMicros = if (done == 0L) 0.0 else totalNanos.get() / done / 1_000.0,
            maxMicros = maxNanos.get() / 1_000.0
        )
    }

    data class Snapshot(
        val received: Long,
        val processed: Long,
        val dropped: Long,
        val failed: Long,
        val avgMicros: Double,
        val maxMicros: Double
    )
}
