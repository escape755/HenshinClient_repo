// Copyright 2026 ESC5P4/Kauri
// Licensed under the Apache License, Version 2.0. See LICENSE.

package com.henshin.client.relay

import androidx.compose.runtime.mutableStateListOf

/** Estado del relay visible en la interfaz: registro de eventos y arranque/parada. */
object RelayController {

    val logLines = mutableStateListOf<String>()

    private val listener = RelayListener(port = 19132) { line -> append(line) }

    val isRunning: Boolean get() = listener.isRunning

    fun toggle() {
        try {
            if (listener.isRunning) listener.stop() else listener.start()
        } catch (e: Exception) {
            append("Error: ${e.message ?: e::class.java.simpleName}")
        }
    }

    private fun append(line: String) {
        val time = java.text.SimpleDateFormat("HH:mm:ss").format(java.util.Date())
        logLines.add(0, "$time  $line")
        if (logLines.size > 50) logLines.removeAt(logLines.lastIndex)
    }
}
