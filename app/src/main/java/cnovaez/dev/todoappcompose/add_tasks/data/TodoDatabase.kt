package cnovaez.dev.todoappcompose.add_tasks.data

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 ** Created by Carlos A. Novaez Guerrero on 10/29/2023 12:05 PM
 ** cnovaez.dev@outlook.com
 **/
@Database(entities = [TaskEntity::class], version = 1)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun tasksDao(): TasksDao
}