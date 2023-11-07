package cnovaez.dev.todoappcompose.add_tasks.ui.model

import cnovaez.dev.todoappcompose.add_tasks.data.TaskEntity

/**
 ** Created by Carlos A. Novaez Guerrero on 10/28/2023 8:47 PM
 ** cnovaez.dev@outlook.com
 **/
data class TaskModel(
    val id: Long = System.currentTimeMillis(),
    var description: String = "",
    var isCompleted: Boolean = false,
    var date: String = "",
    var time: String = "",
    var notify: Boolean = false,
    var repeatRange: String = "",
    var secret_task: Boolean = false,
    val repeat: Boolean = false,
    val important: Boolean = false,
    var followUp: Boolean = true,

) {

    fun toEntity() = TaskEntity(
        id = id,
        description = description,
        isCompleted = isCompleted,
        date = date,
        time = time,
        notify = notify,
        repeatRange = repeatRange,
        secret_task = secret_task,
        repeat = repeat,
        important = important,
        followUp = followUp,
    )
}
