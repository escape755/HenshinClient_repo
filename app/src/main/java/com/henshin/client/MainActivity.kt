// Copyright 2026 ESC5P4/Kauri
// Licensed under the Apache License, Version 2.0. See LICENSE.

package com.henshin.client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
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
import com.henshin.client.ui.DashboardScreen
import com.henshin.client.ui.FpsCounter
import com.henshin.client.ui.HenshinTheme
import com.henshin.client.ui.HomeScreen
import com.henshin.client.ui.SettingsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val prefs = remember { Prefs(applicationContext) }
            HenshinTheme(amoled = prefs.amoled, accentIndex = prefs.accentIndex) {
                HenshinApp(prefs)
            }
        }
    }
}

private enum class Tab(val label: String) { HOME("Inicio"), DASHBOARD("Panel"), SETTINGS("Ajustes") }

@Composable
private fun HenshinApp(prefs: Prefs) {
    var tab by remember { mutableStateOf(Tab.HOME) }
    Scaffold(
        bottomBar = {
            NavigationBar {
                Tab.entries.forEach { t ->
                    NavigationBarItem(
                        selected = tab == t,
                        onClick = { tab = t },
                        icon = { Text(t.label.take(1)) },
                        label = { Text(t.label) }
                    )
                }
            }
        }
    ) { inner ->
        Box(modifier = Modifier.padding(inner).fillMaxSize()) {
            when (tab) {
                Tab.HOME -> HomeScreen()
                Tab.DASHBOARD -> DashboardScreen(prefs)
                Tab.SETTINGS -> SettingsScreen(prefs)
            }
            if (prefs.showFps) {
                FpsCounter(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp))
            }
        }
    }
}
