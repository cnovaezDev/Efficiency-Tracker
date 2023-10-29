package cnovaez.dev.todoappcompose.add_tasks.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cnovaez.dev.todoappcompose.add_tasks.ui.model.TaskModel
import cnovaez.dev.todoappcompose.core.DatePickerView
import cnovaez.dev.todoappcompose.utils.MODE_DARK
import cnovaez.dev.todoappcompose.utils.MODE_LIGHT
import cnovaez.dev.todoappcompose.utils.getMode
import cnovaez.dev.todoappcompose.utils.setMode
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 ** Created by Carlos A. Novaez Guerrero on 10/28/2023 5:07 PM
 ** cnovaez.dev@outlook.com
 **/

@Composable
fun TasksScreen(taskViewModel: TaskViewModel) {
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
                snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
            )
            { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    //? Toggle Night Mode
                    ToggleNigthModButton(
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
                                TaskModel(
                                    description = it,
                                    date = displayedDate
                                )
                            )
                        }

                    )
                    //? Tasks list
                    TaskList(taskViewModel)

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
fun FilterComponent(taskViewModel: TaskViewModel) {
    val searchQuery by taskViewModel.searchQuery.observeAsState(initial = "")
    val showFilter by taskViewModel.showFilter.observeAsState(initial = false)

    val colorTheme by taskViewModel.nightMode.observeAsState(initial = false)
    if (!showFilter) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = "",
            modifier = Modifier
                .padding(start = 8.dp, top = 48.dp)
                .clickable { taskViewModel.showFilter(true) }
        )

    } else {
        Row(Modifier.fillMaxWidth()) {
            Badge(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 4.dp, top = 40.dp),
                containerColor = if (colorTheme) Color.DarkGray else Color.LightGray
                    
            ) {

                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clickable { taskViewModel.showFilter(false) }
                )

                //TextField sin el borde exterior
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { taskViewModel.updateSearchQuery(it) },
                    placeholder = {
                        Text(
                            text = "Search",
                            fontSize = 10.sp,
                            color = if (colorTheme) Color.DarkGray else Color.LightGray,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    },
                    modifier = Modifier
                        .height(48.dp)
                        .weight(1f)
                        .padding(start = 8.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedTextColor = if (colorTheme) Color.White else Color.Black

                    ),
                    textStyle = TextStyle(fontSize = 10.sp)

                )
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable { taskViewModel.updateSearchQuery("") })

            }
        }

    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TaskList(taskViewModel: TaskViewModel) {
    val filter by taskViewModel.showFilter.observeAsState(initial = false)
    val all_tasks = taskViewModel.taskList.sortedBy { it.isCompleted }.groupBy { it.isCompleted }
    LazyColumn(Modifier.padding(top = if (filter) 88.dp else 72.dp)) {
        all_tasks.forEach { (isCompleted, tasks) ->

            stickyHeader {
                Badge(
                    modifier = Modifier.padding(8.dp), containerColor = (if (isCompleted) Color(
                        0xFF70BD91
                    ) else Color(0xFFF58484))
                ) {
                    Space(i = 8)
                    Text(
                        text = (if (isCompleted) "Tasks Completed " else "Tasks Pending ") + "${
                            ((tasks.size * 100) / taskViewModel.taskList.size).toDouble()
                                .roundToInt()

                        }%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp),
                        color = if (taskViewModel.nightMode.value == true) Color.DarkGray else Color.Black
                    )
                    Space(i = 8)
                }
            }

            items(tasks, key = { it.id }) { task ->
                TaskItem(task = task, taskViewModel)
            }
        }
    }
}

@Composable
fun TaskItem(task: TaskModel, taskViewModel: TaskViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = { taskViewModel.onItemLongPress(task) }) {

                }
            }
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = task.description,
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
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
        Icon(imageVector = Icons.AutoMirrored.Filled.NoteAdd, contentDescription = "")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskDialg(
    viewModel: TaskViewModel,
    onDismissReques: () -> Unit,
    onTaskAdded: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val show by viewModel.addTaskDialog.observeAsState(initial = false)
    var taskContent by rememberSaveable {
        mutableStateOf("")
    }
    if (show) {
        Dialog(onDismissRequest = { onDismissReques() }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = "New Task", modifier = Modifier.padding(8.dp))
                Space(16)
                TextField(
                    value = taskContent,
                    onValueChange = { taskContent = it },
                    placeholder = { Text(text = "Task Content", color = Color.LightGray) },
                    modifier = Modifier.padding(8.dp)
                )
                Space(i = 16)
                Button(
                    onClick = {
                        if (validateInfo(taskContent)) {
                            onTaskAdded(taskContent)
                            taskContent = ""
                        } else {
                            scope.launch {
                                viewModel.snackBarHostState.showSnackbar("Task content can't be empty")
                            }
                        }
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

fun validateInfo(taskContent: String) = taskContent.isNotEmpty()

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