// Copyright 2026 ESC5P4/Kauri
// Licensed under the Apache License, Version 2.0. See LICENSE.

package com.henshin.client.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Colores de acento que puede elegir el usuario. */
val Accents: List<Pair<String, Color>> = listOf(
    "Plata" to Color(0xFFC0C6CF),
    "Azul" to Color(0xFF6FA8FF),
    "Morado" to Color(0xFF9B6DFF),
    "Rojo" to Color(0xFFFF6B6B),
    "Verde" to Color(0xFF6BE58F)
)

@Composable
fun HenshinTheme(amoled: Boolean, accentIndex: Int, content: @Composable () -> Unit) {
    val background = if (amoled) Color(0xFF000000) else Color(0xFF121212)
    val card = if (amoled) Color(0xFF141414) else Color(0xFF1E1E1E)
    val accent = Accents[accentIndex].second
    val scheme = darkColorScheme(
        background = background,
        surface = background,
        surfaceVariant = card,
        primary = accent,
        onPrimary = Color(0xFF000000),
        onBackground = Color(0xFFF4F5F7),
        onSurface = Color(0xFFF4F5F7),
        onSurfaceVariant = Color(0xFF9AA0A9),
        secondaryContainer = if (amoled) Color(0xFF1E1E1E) else Color(0xFF2A2A2A),
        onSecondaryContainer = Color(0xFFF4F5F7),
        outline = Color(0xFF2A2A2A)
    )
    MaterialTheme(colorScheme = scheme, content = content)
}
