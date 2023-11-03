package cnovaez.dev.todoappcompose.add_tasks.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import cnovaez.dev.todoappcompose.add_tasks.ui.model.TaskModel

@Entity(tableName = "tasks", primaryKeys = ["id"])
data class TaskEntity(
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "description")
    var description: String = "",
    @ColumnInfo(name = "is_completed")
    var isCompleted: Boolean = false,
    @ColumnInfo(name = "date_of_note")
    var date: String,
    @ColumnInfo(name = "time_of_note")
    var time: String,
    @ColumnInfo(name = "notify")
    var notify: Boolean = false,
    @ColumnInfo(name = "repeat_range")
    var repeatRange: String = "",
    @ColumnInfo(name = "secret_task")
    var secret_task: Boolean = false,

    ) {
    fun toModel() = TaskModel(
        id = id,
        description = description,
        isCompleted = isCompleted,
        date = date,
        time = time,
        notify = notify,
        repeatRange = repeatRange,
        secret_task = secret_task
    )
}

