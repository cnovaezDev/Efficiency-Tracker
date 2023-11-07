package cnovaez.dev.todoappcompose.add_tasks.data

import androidx.room.Database
import androidx.room.RoomDatabase
import cnovaez.dev.todoappcompose.login.data.LoginDao
import cnovaez.dev.todoappcompose.login.data.LoginEntity

/**
 ** Created by Carlos A. Novaez Guerrero on 10/29/2023 12:05 PM
 ** cnovaez.dev@outlook.com
 **/
@Database(entities = [TaskEntity::class, LoginEntity::class], version = 4)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun tasksDao(): TasksDao

    abstract fun loginDao(): LoginDao
}