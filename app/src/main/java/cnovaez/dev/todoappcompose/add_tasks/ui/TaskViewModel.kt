package cnovaez.dev.todoappcompose.add_tasks.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cnovaez.dev.todoappcompose.add_tasks.ui.model.TaskModel
import cnovaez.dev.todoappcompose.add_tasks.use_cases.DeleteTaskUseCase
import cnovaez.dev.todoappcompose.add_tasks.use_cases.GetTaskByIdUseCase
import cnovaez.dev.todoappcompose.add_tasks.use_cases.GetTasksByFilterUseCase
import cnovaez.dev.todoappcompose.add_tasks.use_cases.GetTasksUseCase
import cnovaez.dev.todoappcompose.add_tasks.use_cases.InsertTaskUseCase
import cnovaez.dev.todoappcompose.add_tasks.use_cases.UpdateTaskUseCase
import cnovaez.dev.todoappcompose.utils.logs.LogError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

/**
 ** Created by Carlos A. Novaez Guerrero on 10/28/2023 5:09 PM
 ** cnovaez.dev@outlook.com
 **/
@HiltViewModel
class TaskViewModel @Inject constructor(
    private val insertTaskUseCase: InsertTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val getTasksUseCase: GetTasksUseCase,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val getTaskByFilterUseCase: GetTasksByFilterUseCase,

    ) : ViewModel() {

    val selectedTime = MutableLiveData("")
    val searchQuery = MutableLiveData("")
    val snackBarHostState = SnackbarHostState()
    val today: String by lazy {
        getCurrentDateFormatted()
    }


    private val _showFilter = MutableLiveData(false)

    private var _showAddTaskDialog = MutableLiveData<Boolean>()
    private var _nightMode = MutableLiveData<Boolean>()
    private var _taskList = mutableStateListOf<TaskModel>()
    private var _displayedDate = MutableLiveData<String>()
    private var _showDatePicker = MutableLiveData<Boolean>()
    private val _showTimePicker = MutableLiveData<Boolean>()

    init {
        loadTasksList()
    }

    val showFilter: LiveData<Boolean>
        get() = _showFilter
    val addTaskDialog: LiveData<Boolean>
        get() = _showAddTaskDialog

    val nightMode: LiveData<Boolean>
        get() = _nightMode

    val taskList: List<TaskModel>
        get() = _taskList
    val displayedDate: LiveData<String>
        get() = _displayedDate

    val showDatePicker: LiveData<Boolean>
        get() = _showDatePicker

    val showTimePicker: LiveData<Boolean>
        get() = _showTimePicker

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
        viewModelScope.launch {
            insertTaskUseCase(taskModel.toEntity())
            loadTasksList()
        }
        hideNewTaskDialog()
    }

    private fun loadTasksList() {
        try {
            viewModelScope.launch {
                _taskList.clear()
                val tasks = getTasksUseCase(_displayedDate.value ?: today)
                _taskList.addAll(if (tasks.isNotEmpty()) tasks.map { it.toModel() } else emptyList())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateTaskCheckState(task: TaskModel) {
        try {
            val index = _taskList.indexOfFirst { it.id == task.id }
            val neo_task = task.copy(isCompleted = !task.isCompleted)

            //? To avoid the flickering effect when the checkbox is clicked
            _taskList[index] = neo_task

            viewModelScope.launch {
                updateTaskUseCase(task.toEntity())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogError("Error updating task", e)
        }
    }

    fun onItemLongPress(task: TaskModel) {
        viewModelScope.launch {
            deleteTaskUseCase(task.toEntity())
            loadTasksList()
        }
    }

    fun getCurrentDateFormatted(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    fun displayedDate(stringDate: String) {
        _displayedDate.value = stringDate
    }

    fun showDatePicker(value: Boolean) {
        _showDatePicker.value = value
    }

    fun reloadTasksList() {
        loadTasksList()
    }

    fun showFilter(value: Boolean) {
        _showFilter.value = value
    }

    fun showTimePicker(value: Boolean) {
        _showTimePicker.value = value
    }

    fun updateSearchQuery(filter: String) {
        searchQuery.value = filter
        viewModelScope.launch {
            val filterTasks = getTaskByFilterUseCase(filter.trim(), _displayedDate.value ?: today)
            _taskList.clear()
            if (filterTasks.isNotEmpty()) {
                _taskList.addAll(filterTasks.map { it.toModel() })
            }
        }
    }

}
