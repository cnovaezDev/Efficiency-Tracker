package cnovaez.dev.todoappcompose.utils

import android.content.Context

/**
 ** Created by Carlos A. Novaez Guerrero on 10/28/2023 7:19 PM
 ** cnovaez.dev@outlook.com
 **/

const val CHANNEL_ID = "productivity_app_channel"
const val defaultValueTimer = "-- : --"
var NOTIFICATION_ID = 1

const val MODE_LIGHT = 0
const val MODE_DARK = 1

fun Context.getDarkModePreferences() = getSharedPreferences("dark_mode", Context.MODE_PRIVATE)

// Define una clave para almacenar la configuración del modo (claro u oscuro)
private val MODE_KEY = "mode"

fun getMode(context: Context): Int {
    return context.getDarkModePreferences()
        .getInt(MODE_KEY, MODE_LIGHT) // Valor predeterminado: Modo Claro
}

fun setMode(context: Context, mode: Int) {
    context.getDarkModePreferences().edit().putInt(MODE_KEY, mode).apply()
}


