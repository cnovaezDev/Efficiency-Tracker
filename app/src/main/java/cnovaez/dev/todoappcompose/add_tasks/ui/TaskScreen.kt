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
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cnovaez.dev.todoappcompose.add_tasks.ui.model.TaskModel
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
        }
    }

}

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
