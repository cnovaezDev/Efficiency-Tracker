package cnovaez.dev.todoappcompose.add_tasks.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
<<<<<<< Updated upstream
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
=======
>>>>>>> Stashed changes
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
<<<<<<< Updated upstream
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
=======
import androidx.compose.runtime.produceState
>>>>>>> Stashed changes
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
<<<<<<< Updated upstream
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cnovaez.dev.todoappcompose.add_tasks.ui.model.TaskModel
=======
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import cnovaez.dev.todoappcompose.add_tasks.ui.components.FilterComponent
import cnovaez.dev.todoappcompose.add_tasks.ui.components.NewTaskDialg
import cnovaez.dev.todoappcompose.add_tasks.ui.components.TaskList
import cnovaez.dev.todoappcompose.add_tasks.ui.model.TaskModel
import cnovaez.dev.todoappcompose.core.AddBannerComponent
import cnovaez.dev.todoappcompose.core.DatePickerView
>>>>>>> Stashed changes
import cnovaez.dev.todoappcompose.utils.MODE_DARK
import cnovaez.dev.todoappcompose.utils.MODE_LIGHT
import cnovaez.dev.todoappcompose.utils.getMode
import cnovaez.dev.todoappcompose.utils.setMode

/**
 ** Created by Carlos A. Novaez Guerrero on 10/28/2023 5:07 PM
 ** cnovaez.dev@outlook.com
 **/
@Composable
fun TasksScreen(taskViewModel: TaskViewModel) {
<<<<<<< Updated upstream
    val context = LocalContext.current
    val mode = getMode(context)
    val nightMode by taskViewModel.nightMode.observeAsState(initial = mode == MODE_DARK)
    MaterialTheme(
        colorScheme = if (nightMode) darkColorScheme() else lightColorScheme(),
    ) {
        Surface(modifier = Modifier.fillMaxSize())
        {
            Box(modifier = Modifier.fillMaxSize()) {
                ToggleNigthModButton(
                    nightMode,
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) { taskViewModel.toggleNightMode(it) }
                NewTaskDialg(
                    viewModel = taskViewModel,
                    onDismissReques = { taskViewModel.hideNewTaskDialog() },
                    onTaskAdded = {
                        taskViewModel.createNewTask(TaskModel(description = it))
                    }

                )
                TaskList(taskViewModel)
                AddTaskFoab(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    taskViewModel = taskViewModel
                )

            }
=======
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val uiState by produceState<TasksUiState>(
        initialValue = TasksUiState.Loading,
        key1 = lifecycle,
        key2 = taskViewModel
    ) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            taskViewModel.uiState.collect { value = it }
>>>>>>> Stashed changes
        }
    }

    when (uiState) {
        is TasksUiState.Error -> {
        }

        TasksUiState.Loading -> {

        }
        is TasksUiState.Success -> {
            MainScreen((uiState as TasksUiState.Success).tasks,taskViewModel = taskViewModel)
        }
    }
}

<<<<<<< Updated upstream
@Composable
fun TaskList(taskViewModel: TaskViewModel) {
    val tasks = taskViewModel.taskList
    LazyColumn(Modifier.padding(top = 40.dp)) {
        items(tasks, key = { it.id }) { task ->
            TaskItem(task = task, taskViewModel)
        }
    }
}

@Composable
fun TaskItem(task: TaskModel, taskViewModel: TaskViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp).pointerInput(Unit) {
                detectTapGestures(onLongPress = {taskViewModel.onItemLongPress(task)}) {

                }
            }
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = task.description, modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            )
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { taskViewModel.updateTaskCheckState(task) })
        }
    }
}


@Composable
fun ToggleNigthModButton(nightMode: Boolean, modifier: Modifier, toggleMode: (Boolean) -> Unit) {
    val context = LocalContext.current
    Icon(imageVector = if (nightMode) Icons.Filled.DarkMode else Icons.Filled.LightMode,
        contentDescription = "",
        modifier = modifier.clickable {
            setMode(
                context,
                if (nightMode) MODE_LIGHT else MODE_DARK
            )
            toggleMode(!nightMode)
        })
}

@Composable
fun AddTaskFoab(modifier: Modifier, taskViewModel: TaskViewModel) {
    FloatingActionButton(
        onClick = { taskViewModel.showNewTaskDialog() },
        modifier = modifier.padding(16.dp)
    ) {
        Icon(imageVector = Icons.Filled.NoteAdd, contentDescription = "")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskDialg(
    viewModel: TaskViewModel,
    onDismissReques: () -> Unit,
    onTaskAdded: (String) -> Unit
) {
    val show by viewModel.addTaskDialog.observeAsState(initial = false)
    var taskContent by rememberSaveable {
        mutableStateOf("")
    }
    if (show) {
        Dialog(onDismissRequest = { onDismissReques() }) {
            Card(modifier = Modifier
                .fillMaxWidth()
            ) {
                Text(text = "New Task", modifier = Modifier.padding(8.dp))
                Space(16)
                TextField(
                    value = taskContent,
                    onValueChange = { taskContent = it },
                    placeholder = { Text(text = "Task Content") },
                    modifier = Modifier.padding(8.dp)
                )
                Space(i = 16)
                Button(
                    onClick = {
                        onTaskAdded(taskContent)
                        taskContent = ""
                    }, modifier = Modifier
                        .align(Alignment.End)
                        .padding(8.dp)
                ) {
                    Text(text = "Add Task")
                }
            }
        }
    }
}

@Composable
fun Space(i: Int) {
    Spacer(modifier = Modifier.size(i.dp))
}
=======
    @Composable
    fun MainScreen(tasks: List<TaskModel>, taskViewModel: TaskViewModel) {
        val context = LocalContext.current
        val mode = getMode(context)
        val nightMode by taskViewModel.nightMode.observeAsState(initial = mode == MODE_DARK)
        val showDatePicker by taskViewModel.showDatePicker.observeAsState(initial = false)
        val displayedDate by taskViewModel.displayedDate.observeAsState(initial = taskViewModel.today)
        val snackBarHostState = taskViewModel.snackBarHostState

        MaterialTheme(
            colorScheme = if (nightMode) darkColorScheme() else lightColorScheme(),
        ) {
            Surface(modifier = Modifier.fillMaxSize())
            {


                Scaffold(
                    snackbarHost = {
                        SnackbarHost(
                            hostState = snackBarHostState,
                            modifier = Modifier.statusBarsPadding()
                        )
                    },
                    bottomBar = {
                        AddBannerComponent(
                            adId = "ca-app-pub-3940256099942544/6300978111"
                        )
                    },
                    modifier = Modifier.fillMaxSize()


                )
                { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        //? Toggle Night Mode
                        ToggleNightModeButton(
                            nightMode,
                            Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                        ) { taskViewModel.toggleNightMode(it) }

                        //? New Task Dialog
                        NewTaskDialg(
                            viewModel = taskViewModel,
                            onDismissReques = { taskViewModel.hideNewTaskDialog() },
                            onTaskAdded = {
                                taskViewModel.createNewTask(
                                    it,
                                    context
                                )
                            }

                        )
                        //? Tasks list
                        TaskList(tasks, taskViewModel)

                        //? New Task Foab
                        AddTaskFoab(
                            modifier = Modifier.align(Alignment.BottomEnd),
                            taskViewModel = taskViewModel
                        )

                        //? Date Picker
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp)
                                .clickable { taskViewModel.showDatePicker(true) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.EditCalendar,
                                contentDescription = "",
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = displayedDate,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }


                        if (showDatePicker) {
                            DatePickerView(
                                selectedDate = displayedDate, onDateSelection = {
                                    taskViewModel.displayedDate(it)
                                    taskViewModel.showDatePicker(false)
                                    taskViewModel.reloadTasksList()
                                }, onDismissRequest = {
                                    taskViewModel.showDatePicker(false)
                                })
                        }


                        FilterComponent(taskViewModel)

                    }
                }
            }
        }

    }


    @Composable
    fun ToggleNightModeButton(
        nightMode: Boolean,
        modifier: Modifier,
        toggleMode: (Boolean) -> Unit
    ) {
        val context = LocalContext.current
        Icon(imageVector = if (nightMode) Icons.Filled.DarkMode else Icons.Filled.LightMode,
            contentDescription = "",
            modifier = modifier.clickable {
                setMode(
                    context,
                    if (nightMode) MODE_LIGHT else MODE_DARK
                )
                toggleMode(!nightMode)
            })
    }

    @Composable
    fun AddTaskFoab(modifier: Modifier, taskViewModel: TaskViewModel) {
        FloatingActionButton(
            onClick = { taskViewModel.showNewTaskDialog() },
            modifier = modifier.padding(16.dp)
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.NoteAdd, contentDescription = "")
        }
    }


    @Composable
    fun Space(i: Int) {
        Spacer(modifier = Modifier.size(i.dp))
    }


    @Preview
    @Composable
    fun TestComponent() {
        Row(
            Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {

            //Texto con raya en el medio
            Text(
                text = "Test Component",
                modifier = Modifier.padding(8.dp),
                textDecoration = TextDecoration.LineThrough
            )
        }
    }
>>>>>>> Stashed changes
