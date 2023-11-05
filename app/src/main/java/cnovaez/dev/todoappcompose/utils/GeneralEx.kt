package cnovaez.dev.todoappcompose.utils

import android.app.Activity
import android.content.Context
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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


fun setLanguage(context: Context, lang: String) {
    context.getDarkModePreferences().edit().putString("lang", lang).apply()
}

fun getLanguage(context: Context): String {
    return context.getDarkModePreferences().getString("lang", "na") ?: "na"
}


fun validateContent(taskContent: String) = taskContent.isNotEmpty()
fun validateNotificationTime(time: String) = time != defaultValueTimer


fun isDateEarlyThanToday(dateString: String): Boolean {
//    LogInfo("isDateToday dateString: $dateString")
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.forLanguageTag("es-ES"))
    val today = dateFormat.format(calendar.time)

    val selectedDate = dateFormat.format(dateFormat.parse(dateString))

    return today > selectedDate
}

fun isDateToday(dateString: String): Boolean {
//    LogInfo("isDateToday dateString: $dateString")
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.forLanguageTag("es-ES"))
    val today = dateFormat.format(calendar.time)

    val selectedDate = dateFormat.format(dateFormat.parse(dateString))

    return selectedDate >= today
}

fun isDateBiggerThanToday(dateString: String): Boolean {
//    LogInfo("isDateToday dateString: $dateString")
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.forLanguageTag("es-ES"))
    val today = calendar.time

    val selectedDate = dateFormat.parse(dateString)
//    LogInfo("isDateBiggerThanToday today: ${dateFormat.format(today)}")
//    LogInfo("isDateBiggerThanToday selectedDate: ${dateFormat.format(selectedDate)}")
    return selectedDate > today
}

fun isTimeValid(time: String, dateString: String): Boolean {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("hh:mm a", Locale.forLanguageTag("es-ES"))
    val currentTime = dateFormat.parse(dateFormat.format(calendar.time))

    val selectedTime = dateFormat.parse(time)

//    LogInfo("isTimeValid today: ${dateFormat.format(currentTime)}")
//    LogInfo("isTimeValid selectedDate: ${dateFormat.format(selectedTime)}")
//    LogInfo("Is time valid: ${selectedTime >= currentTime} || ${isDateBiggerThanToday(dateString)}")

    return ((selectedTime >= currentTime) || isDateBiggerThanToday(dateString))
}

fun setLocale(activity: Activity, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)

    val resources = activity.resources
    val config = resources.configuration
    config.setLocale(locale)

    resources.updateConfiguration(config, resources.displayMetrics)
}


