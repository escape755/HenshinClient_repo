// Copyright 2026 ESC5P4/Kauri
// Licensed under the Apache License, Version 2.0. See LICENSE.

package com.henshin.client.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.henshin.client.data.Prefs
import com.henshin.client.data.Versions

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Henshin Client", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
        HenshinCard(title = "Estado") {
            StatRow("Estado", "Listo")
            StatRow("Minecraft", "No instalado")
            StatRow("Versión", "0.1.0")
        }
        HenshinCard(title = "Jugar") {
            Text(
                "La instalación y el lanzamiento del juego todavía no están disponibles.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(12.dp))
            Button(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth()) { Text("Jugar (próximamente)") }
        }
    }
}

@Composable
fun DashboardScreen(prefs: Prefs) {
    val context = LocalContext.current
    var message by remember { mutableStateOf("") }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            message = try {
                context.contentResolver.openOutputStream(uri)?.use { it.write(prefs.exportJson().toByteArray()) }
                "Respaldo guardado."
            } catch (e: Exception) {
                "No se pudo guardar el respaldo."
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            message = try {
                val text = context.contentResolver.openInputStream(uri)?.use { it.readBytes().decodeToString() }.orEmpty()
                if (prefs.importJson(text)) "Respaldo restaurado." else "El archivo no es un respaldo de Henshin."
            } catch (e: Exception) {
                "No se pudo leer el archivo."
            }
        }
    }

    val versions = Versions.installed()

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Panel", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)

        HenshinCard(title = "Estadísticas rápidas") {
            StatRow("Estado", "Listo")
            StatRow("Minecraft", if (versions.isEmpty()) "No instalado" else "${versions.size} versión(es)")
            StatRow("Launcher", "Henshin")
        }

        HenshinCard(title = "Copia de seguridad") {
            Text(
                "Guarda o restaura tus ajustes de Henshin en un archivo .json.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { exportLauncher.launch("henshin-backup.json") }, modifier = Modifier.weight(1f)) {
                    Text("Crear respaldo")
                }
                OutlinedButton(onClick = { importLauncher.launch(arrayOf("application/json")) }, modifier = Modifier.weight(1f)) {
                    Text("Importar")
                }
            }
            if (message.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(message, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
            }
        }

        HenshinCard(title = "Versiones instaladas") {
            if (versions.isEmpty()) {
                Text(
                    "Aún no tienes versiones instaladas.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                versions.forEach { ItemRow(it) }
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth()) { Text("Instalar versión (próximamente)") }
        }
    }
}

@Composable
fun SettingsScreen(prefs: Prefs) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Ajustes", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)

        HenshinCard(title = "Tema") {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Tema AMOLED", color = MaterialTheme.colorScheme.onSurface)
                    Text("Fondo negro puro", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                }
                Switch(checked = prefs.amoled, onCheckedChange = { prefs.setAmoled(it) })
            }
            Spacer(Modifier.height(16.dp))
            Text("Color de acento", color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Accents.forEachIndexed { index, (_, color) ->
                    val selected = prefs.accentIndex == index
                    Box(
                        modifier = Modifier
                            .size(if (selected) 40.dp else 34.dp)
                            .background(color, CircleShape)
                            .clickable { prefs.setAccent(index) }
                    )
                }
            }
        }

        HenshinCard(title = "Rendimiento") {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Mostrar FPS", color = MaterialTheme.colorScheme.onSurface)
                    Text("Cuadros por segundo de Henshin", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                }
                Switch(checked = prefs.showFps, onCheckedChange = { prefs.setShowFps(it) })
            }
        }

        HenshinCard(title = "Acerca de") {
            StatRow("Versión", "0.1.0")
            StatRow("Licencia", "Apache 2.0")
            StatRow("Autor", "ESC5P4/Kauri")
        }
    }
}
