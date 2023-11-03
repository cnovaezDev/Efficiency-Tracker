package cnovaez.dev.todoappcompose.utils.logs

import android.content.Context
import android.util.Log
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 ** Created by Carlos A. Novaez Guerrero on 3/9/2023 8:09 AM
 ** cnovaez.dev@outlook.com
 **/

class FileLoggingTree(private val context: Context) : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val formattedMessage = String.format("%s: %s\n", getDateAndTime(), " DEBUG: $message")
        Log.i("DEBUG: ", formattedMessage)
        when (priority) {
            Log.DEBUG -> {
//                Log.d("DEBUG: ", formattedMessage)
                // Aquí puedes realizar acciones específicas para un log de nivel DEBUG
            }

            Log.INFO -> {
//                Log.i("DEBUG: ", formattedMessage)
                // Aquí puedes realizar acciones específicas para un log de nivel INFO
            }

            Log.WARN -> {
//                Log.w("WARNING: ", formattedMessage)
                // Aquí puedes realizar acciones específicas para un log de nivel WARN
            }

            Log.ERROR -> {
//                Log.e("ERROR", formattedMessage, t)
                // Aquí puedes realizar acciones específicas para un log de nivel ERROR
            }

            else -> {
//                Log.v(tag, formattedMessage)
                // Aquí puedes realizar acciones para otros niveles de log no especificados
            }
        }
//        writeLogsToFile(context, formattedMessage)
    }


    fun getDateAndTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }


//    private fun writeLogsToFile(context: Context, logs: String) {
//        val externalStorageDir = getExternalLogDir(context)
//        if (!externalStorageDir.exists()) {
//            externalStorageDir.mkdirs()
//        }
//        val logFile = File("$externalStorageDir", "pv_logs_${getDate()}.log")
//        logFile.appendText(logs)
//    }
}