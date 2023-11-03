package cnovaez.dev.todoappcompose.add_tasks.ui

import cnovaez.dev.todoappcompose.add_tasks.ui.model.TaskModel

/**
 ** Created by Carlos A. Novaez Guerrero on 11/3/2023 11:27 AM
 ** cnovaez.dev@outlook.com
 **/
sealed interface TasksUiState{
    object Loading: TasksUiState
    data class Success(val tasks: List<TaskModel>): TasksUiState
    data class Error(val throwable: Throwable): TasksUiState
}