package cnovaez.dev.todoappcompose.utils

import android.app.Activity
import android.content.Context
import cnovaez.dev.todoappcompose.utils.logs.LogInfo
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

fun setDailyNotify(context: Context, notify: Boolean) {
    context.getDarkModePreferences().edit().putBoolean("notify", notify).apply()
}

fun getDailyNotify(context: Context): Boolean {
    return context.getDarkModePreferences().getBoolean("notify", false) ?: false
}


fun validateContent(taskContent: String) = taskContent.isNotEmpty()
fun validateNotificationTime(time: String) = time != defaultValueTimer


fun isDateEarlyThanToday(dateString: String, context: Context): Boolean {
//    LogInfo("isDateToday dateString: $dateString")
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", getLocaleByLanguage(context))
    val today = dateFormat.format(calendar.time)

    val selectedDate = dateFormat.format(dateFormat.parse(dateString))

    return today > selectedDate
}

fun isDateToday(dateString: String, context: Context): Boolean {
//    LogInfo("isDateToday dateString: $dateString")
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", getLocaleByLanguage(context))
    val today = dateFormat.format(calendar.time)

    val selectedDate = dateFormat.format(dateFormat.parse(dateString))

    return selectedDate >= today
}

fun isDateBiggerThanToday(dateString: String, context: Context): Boolean {
//    LogInfo("isDateToday dateString: $dateString")
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", getLocaleByLanguage(context))
    val today = calendar.time

    val selectedDate = dateFormat.parse(dateString)
//    LogInfo("isDateBiggerThanToday today: ${dateFormat.format(today)}")
//    LogInfo("isDateBiggerThanToday selectedDate: ${dateFormat.format(selectedDate)}")
    return selectedDate > today
}

fun isTimeValid(time: String, dateString: String, context: Context): Boolean {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("hh:mm a", identificarLocaleDesdeFormatoHora(time)!!)
    val currentTime = dateFormat.parse(dateFormat.format(calendar.time))

    val selectedTime = dateFormat.parse(time)

//    LogInfo("isTimeValid today: ${dateFormat.format(currentTime)}")
//    LogInfo("isTimeValid selectedDate: ${dateFormat.format(selectedTime)}")
//    LogInfo("Is time valid: ${selectedTime >= currentTime} || ${isDateBiggerThanToday(dateString)}")

    return ((selectedTime >= currentTime) || isDateBiggerThanToday(dateString, context))
}

fun getLocaleByLanguage(context: Context): Locale {
val lang = getLanguage(context)
    return if (lang == "es") {
        Locale.forLanguageTag("es-ES")
    } else {
        Locale.forLanguageTag("en-US")
    }
}

fun setLocale(activity: Activity, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)

    val resources = activity.resources
    val config = resources.configuration
    config.setLocale(locale)

    resources.updateConfiguration(config, resources.displayMetrics)
}

fun identificarLocaleDesdeFormatoHora(formatoHora: String): Locale? {
    LogInfo("Formato hora: $formatoHora")
    if(formatoHora.contains("a") || formatoHora.contains("p")){
        return Locale.forLanguageTag("es-ES")
    }
    if(formatoHora.contains("AM") || formatoHora.contains("PM")){
        return Locale.forLanguageTag("en-US")
    }
    return null  // Si no se encontró un idioma adecuado
}


