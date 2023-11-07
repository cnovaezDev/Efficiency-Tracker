package cnovaez.dev.todoappcompose.di

import android.content.Context
import androidx.room.Room
import cnovaez.dev.todoappcompose.add_tasks.data.TodoDatabase
import cnovaez.dev.todoappcompose.core.migracion1a2
import cnovaez.dev.todoappcompose.core.migracion2a3
import cnovaez.dev.todoappcompose.core.migracion3a4
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 ** Created by Carlos A. Novaez Guerrero on 10/29/2023 12:11 PM
 ** cnovaez.dev@outlook.com
 **/
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val DATABASE_NAME: String = "productivity_tracker.db"

    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext context: Context): TodoDatabase {
        return Room.databaseBuilder(
            context,
            TodoDatabase::class.java,
            DATABASE_NAME
        )
            .addMigrations(migracion1a2)
            .addMigrations(migracion2a3)
            .addMigrations(migracion3a4)
            .build()
    }

    @Provides
    @Singleton
    fun provideTasksDao(todoDatabase: TodoDatabase) = todoDatabase.tasksDao()

    @Provides
    @Singleton
    fun provideLoginDao(todoDatabase: TodoDatabase) = todoDatabase.loginDao()
}