// Copyright 2026 ESC5P4/Kauri
// Licensed under the Apache License, Version 2.0. See LICENSE.

package com.henshin.client.relay.core

import java.util.concurrent.ConcurrentHashMap

/** Identificador de un chunk: coordenadas y dimensión. */
data class ChunkKey(val x: Int, val z: Int, val dimension: Int)

/**
 * Almacén de chunks. Lectura y escritura concurrentes: los hilos de trabajo escriben
 * y la interfaz lee sin bloquearse entre sí.
 */
class ChunkStore<T : Any> {

    private val chunks = ConcurrentHashMap<ChunkKey, T>()

    fun put(key: ChunkKey, value: T) {
        chunks[key] = value
    }

    fun get(key: ChunkKey): T? = chunks[key]

    fun remove(key: ChunkKey): T? = chunks.remove(key)

    fun size(): Int = chunks.size

    fun clear() = chunks.clear()

    /** Quita chunks lejos del jugador para no crecer sin límite. */
    fun evictFarFrom(centerX: Int, centerZ: Int, dimension: Int, radius: Int) {
        val iterator = chunks.keys.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            if (key.dimension != dimension) {
                iterator.remove()
                continue
            }
            val dx = key.x - centerX
            val dz = key.z - centerZ
            if (dx * dx + dz * dz > radius * radius) iterator.remove()
        }
    }
}
