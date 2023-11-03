package cnovaez.dev.todoappcompose.add_tasks.ui

import android.app.NotificationManager
import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import cnovaez.dev.todoappcompose.add_tasks.ui.model.TaskModel
import cnovaez.dev.todoappcompose.add_tasks.use_cases.DeleteTaskByIdUseCase
import cnovaez.dev.todoappcompose.add_tasks.use_cases.DeleteTaskUseCase
import cnovaez.dev.todoappcompose.add_tasks.use_cases.GetRowIdUseCase
import cnovaez.dev.todoappcompose.add_tasks.use_cases.GetTaskByIdUseCase
import cnovaez.dev.todoappcompose.add_tasks.use_cases.GetTasksByFilterUseCase
import cnovaez.dev.todoappcompose.add_tasks.use_cases.GetTasksUseCase
import cnovaez.dev.todoappcompose.add_tasks.use_cases.InsertTaskUseCase
import cnovaez.dev.todoappcompose.add_tasks.use_cases.UpdateTaskUseCase
import cnovaez.dev.todoappcompose.utils.logs.LogError
import cnovaez.dev.todoappcompose.utils.logs.LogInfo
import cnovaez.dev.todoappcompose.utils.notifications.NotificationWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 ** Created by Carlos A. Novaez Guerrero on 10/28/2023 5:09 PM
 ** cnovaez.dev@outlook.com
 **/
@HiltViewModel
class TaskViewModel @Inject constructor(
    private val insertTaskUseCase: InsertTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val deleteTaskByIdUseCase: DeleteTaskByIdUseCase,
    private val getTasksUseCase: GetTasksUseCase,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val getTaskByFilterUseCase: GetTasksByFilterUseCase,
    private val getRowIdUseCase: GetRowIdUseCase,


    ) : ViewModel() {


    val selectedTime = MutableLiveData("")
    val searchQuery = MutableLiveData("")
    val snackBarHostState = SnackbarHostState()
    val today: String by lazy {
        getCurrentDateFormatted()
    }


    private val _showFilter = MutableLiveData(false)

    private var _showAddTaskDialog = MutableLiveData<Pair<Boolean, TaskModel?>>()
    private var _nightMode = MutableLiveData<Boolean>()
    private var _taskList = mutableStateListOf<TaskModel>()
    private var _displayedDate = MutableLiveData<String>()
    private var _showDatePicker = MutableLiveData<Boolean>()
    private val _showTimePicker = MutableLiveData<Boolean>()
    private val _errorState = MutableLiveData<Boolean>()
    private val _errorStateTimer = MutableLiveData<Boolean>()

    private val _showDeleteSnackBar = MutableLiveData<Pair<Boolean, TaskModel?>>()


    init {
        loadTasksList()
    }

    val showFilter: LiveData<Boolean>
        get() = _showFilter
    val addTaskDialog: LiveData<Pair<Boolean, TaskModel?>>
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
    val errorState: LiveData<Boolean>
        get() = _errorState
    val errorStateTimer: LiveData<Boolean>
        get() = _errorStateTimer

    val showDeleteSnackBar: LiveData<Pair<Boolean, TaskModel?>>
        get() = _showDeleteSnackBar

    fun showNewTaskDialog(task: TaskModel? = null) {
        _showAddTaskDialog.value = Pair(true, task)
    }

    fun hideNewTaskDialog() {
        _showAddTaskDialog.value = Pair(false, null)
    }

    fun toggleNightMode(value: Boolean) {
        _nightMode.value = value
    }

    fun createNewTask(taskModel: TaskModel, context: Context, edit: Boolean) {

        viewModelScope.launch {
            if (!edit) {
                insertTaskUseCase(taskModel.toEntity())
            } else {
                updateTaskUseCase(taskModel.toEntity())
            }
            val notificationId = getRowIdUseCase(taskModel.id)
            loadTasksList()
            if (taskModel.notify) {
                cancelExistingNotification(notificationId, context)
                scheduleNotification(
                    taskModel.date,
                    taskModel.time,
                    taskModel.description,
                    context,
                    notificationId
                )
            }
        }
        hideNewTaskDialog()
    }


    /**
     * Cancel any existing notification with the ID passed as param.
     */
    private fun cancelExistingNotification(notificationId: Int, context: Context) {
        val notificationManager =
            getSystemService(context, NotificationManager::class.java) as NotificationManager
        notificationManager.cancel(notificationId)
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
                updateTaskUseCase(neo_task.toEntity())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogError("Error updating task", e)
        }
    }

    fun deleteTaskFromMemory(task: TaskModel) {
        _taskList.removeIf { task.id == it.id }
    }

    fun deleteTask(task: TaskModel) {
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

    fun scheduleNotification(
        date: String,
        time: String,
        notificationMessage: String,
        context: Context,
        notificationId: Int
    ) {
        LogInfo("Attempting to schedule notification for $date $time with message: $notificationMessage")
        val workManager = WorkManager.getInstance(context)

        val dateTime = combineDateTime(date, time)

        if (dateTime != null) {
            val notificationData = Data.Builder()
                .putString("message", notificationMessage)
                .putInt("notificationId", notificationId)
                .build()

            val notificationRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(dateTime.time - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .setInputData(notificationData)
                .build()

            workManager.enqueue(notificationRequest)
            LogInfo("Notification scheduled successfully")
        }
    }

    fun scheduleRepeatingNotification(
        date: String,
        time: String,
        notificationMessage: String
    ) {
        val workManager = WorkManager.getInstance()

        val dateTime = combineDateTime(date, time)

        if (dateTime != null) {
            val notificationData = Data.Builder()
                .putString("message", notificationMessage)
                .build()

            val currentTime = System.currentTimeMillis()
            val initialDelay = dateTime.time - currentTime
            val repeatInterval = TimeUnit.DAYS.toMillis(1)  // 1 día en milisegundos

            val notificationRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
                repeatInterval,
                TimeUnit.MILLISECONDS
            )
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .setInputData(notificationData)
                .build()

            workManager.enqueueUniquePeriodicWork(
                "repeating_notification",
                ExistingPeriodicWorkPolicy.REPLACE,
                notificationRequest
            )
        }
    }

    private fun combineDateTime(date: String, time: String): Date? {
        val dateTimeString = "$date $time"
        val dateTimeFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())

        return try {
            dateTimeFormat.parse(dateTimeString)
        } catch (e: Exception) {
            // Manejar errores en el formato de fecha y hora
            null
        }
    }

    fun updateErrorState(b: Boolean) {
        _errorState.value = b
    }

    fun updateErrorStateTimer(b: Boolean) {
        _errorStateTimer.value = b
    }

    fun showDeleteSnackBar(b: Boolean, task: TaskModel? = null) {
        _showDeleteSnackBar.value = Pair(b, task)
    }


}
