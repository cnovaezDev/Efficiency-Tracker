package cnovaez.dev.todoappcompose.add_tasks.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cnovaez.dev.todoappcompose.add_tasks.ui.model.TaskModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 ** Created by Carlos A. Novaez Guerrero on 10/28/2023 5:09 PM
 ** cnovaez.dev@outlook.com
 **/
@HiltViewModel
class TaskViewModel @Inject constructor() : ViewModel() {
    private var _showAddTaskDialog = MutableLiveData<Boolean>()
    private var _nightMode = MutableLiveData<Boolean>()
    private var _taskList = mutableStateListOf<TaskModel>()

    val addTaskDialog: LiveData<Boolean>
        get() = _showAddTaskDialog

    val nightMode: LiveData<Boolean>
        get() = _nightMode

    val taskList: List<TaskModel>
        get() = _taskList


    fun showNewTaskDialog() {
        _showAddTaskDialog.value = true
    }

    fun hideNewTaskDialog() {
        _showAddTaskDialog.value = false
    }

    fun toggleNightMode(value: Boolean) {
        _nightMode.value = value
    }

    fun createNewTask(taskModel: TaskModel) {
        _taskList.add(taskModel)
        hideNewTaskDialog()
    }

    fun updateTaskCheckState(task: TaskModel) {
        val index = _taskList.indexOf(task)
        _taskList[index] = task.copy(isCompleted = !task.isCompleted)
    }

    fun onItemLongPress(task: TaskModel) {
        _taskList.removeIf { it.id == task.id }
    }
}
