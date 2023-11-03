package cnovaez.dev.todoappcompose.add_tasks.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 ** Created by Carlos A. Novaez Guerrero on 10/29/2023 12:08 PM
 ** cnovaez.dev@outlook.com
 **/
@Dao
interface TasksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(taskEntity: TaskEntity)

    @Query("UPDATE tasks SET description = :description, is_completed = :isCompleted, date_of_note = :date, time_of_note = :time, secret_task = :secretTask, notify = :notify WHERE id = :id")
    suspend fun updateTask(
        id: Long,
        description: String,
        isCompleted: Boolean,
        date: String,
        time: String,
        secretTask: Boolean,
        notify: Boolean
    )

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: String)

    @Delete
    suspend fun deleteTask(taskEntity: TaskEntity)

    @Query("SELECT * FROM tasks where date_of_note = :date")
    suspend fun getTasks(date: String): List<TaskEntity>

    @Query("SELECT * FROM tasks where secret_task ='true'")
    suspend fun getSecretTasks(): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskEntity

    @Query("SELECT * FROM tasks WHERE description LIKE '%' || :filter || '%' AND date_of_note = :date")
    suspend fun getTaskByFilter(filter: String, date: String): List<TaskEntity>?

    //get rowid by task id
    @Query("SELECT rowid FROM tasks WHERE id = :id")
    suspend fun getRowId(id: Long): Int?
}