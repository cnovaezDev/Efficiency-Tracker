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
import cnovaez.dev.todoappcompose.R
import cnovaez.dev.todoappcompose.add_tasks.ui.model.TaskModel
import cnovaez.dev.todoappcompose.add_tasks.use_cases.DeleteTaskByIdUseCase
import cnovaez.dev.todoappcompose.add_tasks.use_cases.DeleteTaskUseCase
import cnovaez.dev.todoappcompose.add_tasks.use_cases.GetAllTasksUseCase
import cnovaez.dev.todoappcompose.add_tasks.use_cases.GetRowIdUseCase
import cnovaez.dev.todoappcompose.add_tasks.use_cases.GetTaskByIdUseCase
import cnovaez.dev.todoappcompose.add_tasks.use_cases.GetTasksByFilterUseCase
import cnovaez.dev.todoappcompose.add_tasks.use_cases.GetTasksUseCase
import cnovaez.dev.todoappcompose.add_tasks.use_cases.InsertTaskUseCase
import cnovaez.dev.todoappcompose.add_tasks.use_cases.UpdateTaskUseCase
import cnovaez.dev.todoappcompose.utils.curr_context
import cnovaez.dev.todoappcompose.utils.getLocaleByLanguage
import cnovaez.dev.todoappcompose.utils.identificarLocaleDesdeFormatoHora
import cnovaez.dev.todoappcompose.utils.isDateEarlyThanToday
import cnovaez.dev.todoappcompose.utils.logs.LogError
import cnovaez.dev.todoappcompose.utils.logs.LogInfo
import cnovaez.dev.todoappcompose.utils.notifications.NotificationWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Timer
import java.util.TimerTask
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
    private val getAllTasksUseCase: GetAllTasksUseCase,

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
    private var _allTasks = mutableStateListOf<TaskModel>()
    private var _displayedDate = MutableLiveData<String>()
    private var _showDatePicker = MutableLiveData<Boolean>()
    private val _showTimePicker = MutableLiveData<Boolean>()
    private val _errorState = MutableLiveData<Boolean>()
    private val _errorStateTimer = MutableLiveData<Boolean>()
    private val _errorStateDate = MutableLiveData<Boolean>()
    private val _showChart = MutableLiveData<Boolean>()
    private val _showChartLoading = MutableLiveData<Boolean>()

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
    val showChart: LiveData<Boolean>
        get() = _showChart
    val showChartLoading: LiveData<Boolean>
        get() = _showChartLoading

    val taskList: List<TaskModel>
        get() = _taskList

    val alltaskList: List<TaskModel>
        get() = _allTasks
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
    val errorStateDate: LiveData<Boolean>
        get() = _errorStateDate

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
            if (taskModel.notify) {
                cancelExistingNotification(notificationId, context)
                if (taskModel.repeat) {
                    scheduleRepeatingNotification(
                        taskModel.date,
                        taskModel.time,
                        taskModel.description,
                        context,
                        notificationId
                    )
                } else {
                    scheduleNotification(
                        taskModel.date,
                        taskModel.time,
                        taskModel.description,
                        context,
                        notificationId
                    )
                }
            }
            loadTasksList()
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
                val repeatTasks =
                    tasks.filter { it.repeat && isDateEarlyThanToday(it.date, curr_context!!) }
                if (repeatTasks.isEmpty()) {
                    _taskList.addAll(if (tasks.isNotEmpty()) tasks.map { it.toModel() } else emptyList())
                } else {
                    repeatTasks.forEach { task ->
                        val neo_task = task.copy(date = today)
                        updateTaskUseCase(neo_task)
                    }
                    val tasks = getTasksUseCase(_displayedDate.value ?: today)
                    _taskList.addAll(if (tasks.isNotEmpty()) tasks.map { it.toModel() } else emptyList())
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadAllTasks() {
        try {
            viewModelScope.launch {
                _showChartLoading.postValue(false)
                _allTasks.clear()
                val tasks = getAllTasksUseCase()
                _allTasks.addAll(if (tasks.isNotEmpty()) tasks.map { it.toModel() } else emptyList())
                _showChartLoading.postValue(true)
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
            //     _taskList[index] = neo_task

            viewModelScope.launch {
                updateTaskUseCase(neo_task.toEntity())
                loadTasksList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogError("Error updating task", e)
        }
    }

    private var currentDeletedTask: TaskModel? = null
    fun deleteTaskFromMemory(task: TaskModel) {
        try {
            viewModelScope.launch {
                currentDeletedTask = task
                deleteTaskByIdUseCase(task.id)
                loadTasksList()
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
            LogError("Error deleting task from memory", ex)
        }
    }

    fun deleteTask(task: TaskModel, context: Context) {
        viewModelScope.launch {
            val notificationId = getRowIdUseCase(task.id)
            cancelExistingNotification(notificationId, context = context)
            deleteTaskUseCase(task.toEntity())
            loadTasksList()
        }
    }

    fun getCurrentDateFormatted(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", getLocaleByLanguage(curr_context!!))
        return dateFormat.format(calendar.time)
    }

//    fun getCurrentTimeFormatted(): String {
//        val calendar = Calendar.getInstance()
//        val dateFormat = SimpleDateFormat("hh:mm a", identificarLocaleDesdeFormatoHora(time) ?: getLocaleByLanguage(context))
//        return dateFormat.format(calendar.time)
//    }

    fun getTimeFormatted(time: String): String {
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.US)
        return dateFormat.format(dateFormat.parse(time)!!)
    }

    fun displayedDate(stringDate: String) {
        _displayedDate.value = stringDate
    }

    fun showDatePicker(value: Boolean) {
        _showDatePicker.value = value
    }

    fun reloadTasksList() {
        viewModelScope.launch {
            if (currentDeletedTask != null) {
                insertTaskUseCase(currentDeletedTask!!.toEntity())
                currentDeletedTask = null
            }
            loadTasksList()
        }
    }

    fun showFilter(value: Boolean) {
        _showFilter.value = value
    }

    fun showTimePicker(value: Boolean) {
        _showTimePicker.value = value
    }

    var timer: Timer = Timer()

    fun updateSearchQuery(filter: String) {
        searchQuery.value = filter
        timer?.cancel()
        timer = Timer()

        timer?.schedule(object : TimerTask() {
            override fun run() {
                viewModelScope.launch {
                    withContext(Dispatchers.Main) {
                        _taskList.clear()
                    }

                    val filterTasks =
                        getTaskByFilterUseCase(filter.trim(), _displayedDate.value ?: today)

                    if (filterTasks.isNotEmpty()) {
                        withContext(Dispatchers.Main) {

                            _taskList.addAll(filterTasks.map { it.toModel() })
                        }
                    }
                }
            }
        }, 600)

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
        notificationMessage: String,
        context: Context,
        notificationId: Int
    ) {
        LogInfo("Attempting to schedule a repeating notification for $date $time with message: $notificationMessage")

        val workManager = WorkManager.getInstance(context)

        val dateTime = combineDateTime(date, time)

        if (dateTime != null) {
            val notificationData = Data.Builder()
                .putString("message", notificationMessage)
                .putInt("notificationId", notificationId)
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
            LogInfo("Repeating Notification scheduled successfully")
        }
    }


    fun scheduleSystemRepeatingNotification(
        context: Context,
        notificationId: Int = "ProductivityApp".hashCode()
    ) {
        val notificationMessage =
            context.getString(R.string.is_time_to_be_productive_set_your_tasks_for_today)
        LogInfo(
            "Attempting to schedule a repeating notification for ${getCurrentDateFormatted()} ${
                getTimeFormatted(
                    "10:00 AM"
                )
            } with message: $notificationMessage"
        )

        val workManager = WorkManager.getInstance(context)

        val dateTime =
            combineSystemDateTime(getCurrentDateFormatted(), getTimeFormatted("10:00 AM"))

        if (dateTime != null) {
            val notificationData = Data.Builder()
                .putString("message", notificationMessage)
                .putInt("notificationId", notificationId)
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
            LogInfo("Repeating Notification scheduled successfully")
        }
    }

    private fun combineDateTime(date: String, time: String): Date? {
        val dateTimeString = "$date $time"
        val dateTimeFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a",getLocaleByLanguage(curr_context!!))

        return try {
            dateTimeFormat.parse(dateTimeString)
        } catch (e: Exception) {
            // Manejar errores en el formato de fecha y hora
            e.printStackTrace()
            null
        }
    }

    private fun combineSystemDateTime(date: String, time: String): Date? {
        val dateTimeString = "$date $time"
        val dateTimeFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.US)

        return try {
            dateTimeFormat.parse(dateTimeString)
        } catch (e: Exception) {
            // Manejar errores en el formato de fecha y hora
            e.printStackTrace()
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
        if(!b){
            currentDeletedTask = null
        }
        _showDeleteSnackBar.value = Pair(b, task)
    }

    fun updateErrorStateDate(b: Boolean) {
        _errorStateDate.value = b
    }

    fun updateShowChart(b: Boolean) {
        _showChart.value = b
    }

    fun updateShowChartLoading(b: Boolean) {
        _showChartLoading.value = b
    }

}
