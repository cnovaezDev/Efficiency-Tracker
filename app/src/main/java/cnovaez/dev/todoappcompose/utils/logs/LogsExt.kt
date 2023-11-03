package cnovaez.dev.todoappcompose.utils.logs

import timber.log.Timber

/**
 ** Created by Carlos A. Novaez Guerrero on 3/9/2023 9:06 AM
 ** cnovaez.dev@outlook.com
 **/

fun LogError(msg: String) {
    Timber.e(msg)
    //evaluateIfDisplayErrorDialog(msg)
}

fun LogError(msg: String, exception: Exception) {
    Timber.e(exception, "$msg: %s", exception.message ?: "null")
}

fun LogInfo(msg: String) = Timber.d(msg)


