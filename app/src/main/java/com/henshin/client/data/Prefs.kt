// Copyright 2026 ESC5P4/Kauri
// Licensed under the Apache License, Version 2.0. See LICENSE.

package com.henshin.client.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONObject

/** Ajustes de Henshin Client. Se guardan en SharedPreferences y se exportan como respaldo JSON. */
class Prefs(context: Context) {

    private val sp = context.getSharedPreferences("henshin_prefs", Context.MODE_PRIVATE)

    var amoled by mutableStateOf(sp.getBoolean(KEY_AMOLED, true))
        private set

    var accentIndex by mutableStateOf(sp.getInt(KEY_ACCENT, 0).coerceIn(0, ACCENT_COUNT - 1))
        private set

    var showFps by mutableStateOf(sp.getBoolean(KEY_FPS, false))
        private set

    fun setAmoled(value: Boolean) {
        amoled = value
        sp.edit().putBoolean(KEY_AMOLED, value).apply()
    }

    fun setAccent(index: Int) {
        accentIndex = index.coerceIn(0, ACCENT_COUNT - 1)
        sp.edit().putInt(KEY_ACCENT, accentIndex).apply()
    }

    fun setShowFps(value: Boolean) {
        showFps = value
        sp.edit().putBoolean(KEY_FPS, value).apply()
    }

    /** Respaldo: JSON con todos los ajustes. */
    fun exportJson(): String = JSONObject()
        .put("app", "HenshinClient")
        .put("format", 1)
        .put("amoled", amoled)
        .put("accent", accentIndex)
        .put("fps", showFps)
        .toString(2)

    /** Restaura un respaldo. Devuelve false si el archivo no es válido. */
    fun importJson(text: String): Boolean {
        return try {
            val obj = JSONObject(text)
            if (obj.optString("app") != "HenshinClient") return false
            setAmoled(obj.getBoolean("amoled"))
            setAccent(obj.getInt("accent"))
            setShowFps(obj.getBoolean("fps"))
            true
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        const val KEY_AMOLED = "amoled"
        const val KEY_ACCENT = "accent"
        const val KEY_FPS = "fps"
        const val ACCENT_COUNT = 5
    }
}
