// Copyright 2026 ESC5P4/Kauri
// Licensed under the Apache License, Version 2.0. See LICENSE.

package com.henshin.client.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Contador de cuadros por segundo de la propia interfaz de Henshin Client.
 * No mide el juego: el juego todavía no está integrado.
 */
@Composable
fun FpsCounter(modifier: Modifier = Modifier) {
    var fps by mutableIntStateOf(0)

    LaunchedEffect(Unit) {
        var frames = 0
        var windowStart = 0L
        while (true) {
            withFrameNanos { now ->
                if (windowStart == 0L) windowStart = now
                frames++
                if (now - windowStart >= 1_000_000_000L) {
                    fps = frames
                    frames = 0
                    windowStart = now
                }
            }
        }
    }

    Text(
        text = "$fps FPS",
        color = Color.White,
        fontSize = 12.sp,
        modifier = modifier
            .background(Color(0x99000000))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
