package cnovaez.dev.todoappcompose.add_tasks.ui.model

/**
 ** Created by Carlos A. Novaez Guerrero on 10/28/2023 8:47 PM
 ** cnovaez.dev@outlook.com
 **/
data class TaskModel(
    val id: Long = System.currentTimeMillis(),
    var title: String = "",
    var description: String = "",
    var isCompleted: Boolean = false
)
