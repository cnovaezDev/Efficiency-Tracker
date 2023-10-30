package cnovaez.dev.todoappcompose.utils.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 ** Created by Carlos A. Novaez Guerrero on 10/30/2023 3:07 PM
 ** cnovaez.dev@outlook.com
 **/
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
            // Vuelve a programar las notificaciones aquí
        }
    }
}