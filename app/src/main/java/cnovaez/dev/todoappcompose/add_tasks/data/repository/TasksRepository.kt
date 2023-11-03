package cnovaez.dev.todoappcompose.add_tasks.data.repository

import cnovaez.dev.todoappcompose.add_tasks.data.TasksDao
import cnovaez.dev.todoappcompose.add_tasks.ui.model.TaskModel

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import cnovaez.dev.todoappcompose.add_tasks.data.TaskEntity
import cnovaez.dev.todoappcompose.add_tasks.data.TasksDao
import cnovaez.dev.todoappcompose.utils.logs.LogError
import javax.inject.Inject

/**
 ** Created by Carlos A. Novaez Guerrero on 10/29/2023 12:16 PM
 ** cnovaez.dev@outlook.com
 **/

@Singleton
class TasksRepository @Inject constructor(
    private val tasksDao: TasksDao
) {

    suspend fun insertTask(taskEntity: TaskEntity){
        try {
            tasksDao.insertTask(taskEntity)
        } catch (e: Exception) {
            e.printStackTrace()
            LogError("Error inserting task", e)
        }
    }

    suspend fun updateTask(taskEntity: TaskEntity) {
        try {
            tasksDao.updateTask(taskEntity.id, taskEntity.description, taskEntity.isCompleted)
        } catch (e: Exception) {
            e.printStackTrace()
            LogError("Error updating task", e)
        }
    }

    suspend fun deleteTask(taskEntity: TaskEntity) {
        try {
            tasksDao.deleteTask(taskEntity)
        } catch (e: Exception) {
            e.printStackTrace()
            LogError("Error deleting task", e)
        }
    }

    fun getTasks(date: String) : Flow<List<TaskModel>> = tasksDao.getTasks(date).map { items-> items.map { it.toModel() } }


    suspend fun getTaskById(id: Long): TaskEntity? {
        return try {
            tasksDao.getTaskById(id)
        } catch (e: Exception) {
            e.printStackTrace()
            LogError("Error getting task by id", e)
            null
        }
    }

    suspend fun getTaskByFilter(filter: String, date: String): List<TaskEntity> {
        return try {
            if (filter.isNotEmpty()) {
                tasksDao.getTaskByFilter(filter, date) ?: emptyList()
            } else {
                tasksDao.getTasks(date)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogError("Error getting task by filter", e)
            emptyList()
        }
    }

    suspend fun getRowId(id: Long): Int {
        return try {
            tasksDao.getRowId(id) ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
            LogError("Error getting row id", e)
            0
        }
    }
}
