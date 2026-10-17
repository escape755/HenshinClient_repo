// Copyright 2026 ESC5P4/Kauri
// Licensed under the Apache License, Version 2.0. See LICENSE.

package com.henshin.client.relay.core

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Cola de trabajo con hilos propios. La conexión solo encola y vuelve enseguida;
 * el procesamiento pesado ocurre aquí, sin frenar la lectura de red.
 *
 * Si la cola se llena, descarta el trabajo más viejo y cuenta la pérdida en [metrics].
 */
class PacketPipeline(
    capacity: Int = 4096,
    workers: Int = (Runtime.getRuntime().availableProcessors() - 1).coerceIn(1, 4),
    val metrics: RelayMetrics = RelayMetrics()
) {
    private val queue = LinkedBlockingQueue<Runnable>(capacity)
    private val running = AtomicBoolean(false)
    private val threads = List(workers) { i ->
        Thread({ loop() }, "henshin-relay-$i").apply { isDaemon = true }
    }

    fun start() {
        if (!running.compareAndSet(false, true)) return
        threads.forEach { it.start() }
    }

    fun stop() {
        running.set(false)
        threads.forEach { it.interrupt() }
    }

    /** Encola sin bloquear. Devuelve false si hubo que descartar algo. */
    fun submit(job: Runnable): Boolean {
        metrics.onReceived()
        if (queue.offer(job)) return true
        queue.poll()              // descarta el más viejo
        metrics.onDropped()
        return queue.offer(job)
    }

    private fun loop() {
        while (running.get()) {
            val job = try {
                queue.take()
            } catch (e: InterruptedException) {
                break
            }
            val start = System.nanoTime()
            try {
                job.run()
            } catch (e: Exception) {
                metrics.onFailed()
            } finally {
                metrics.onProcessed(System.nanoTime() - start)
            }
        }
    }
}
