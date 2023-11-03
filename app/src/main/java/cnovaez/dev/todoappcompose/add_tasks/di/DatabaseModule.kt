package cnovaez.dev.todoappcompose.add_tasks.di

import android.content.Context
import androidx.room.Room
import cnovaez.dev.todoappcompose.add_tasks.data.TodoDatabase
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
        ).build()
    }

    @Provides
    @Singleton
    fun provideTasksDao(todoDatabase: TodoDatabase) = todoDatabase.tasksDao()
}