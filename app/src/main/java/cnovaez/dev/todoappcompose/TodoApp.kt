package cnovaez.dev.todoappcompose

import android.app.Application
import cnovaez.dev.todoappcompose.utils.logs.FileLoggingTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 ** Created by Carlos A. Novaez Guerrero on 10/28/2023 5:07 PM
 ** cnovaez.dev@outlook.com
 **/
@HiltAndroidApp
class TodoApp: Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(FileLoggingTree(this))
    }
}